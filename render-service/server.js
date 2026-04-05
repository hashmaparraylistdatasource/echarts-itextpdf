const http = require('http');
const fs = require('fs');
const path = require('path');
const childProcess = require('child_process');
const echarts = require('echarts');
const puppeteer = require('puppeteer-core');

const serviceName = 'echarts-itextpdf-render-service';
const protocolVersion = 2;
const host = process.env.ECHARTS_RENDER_HOST || '127.0.0.1';
const port = parseInt(process.env.ECHARTS_RENDER_PORT || process.argv[2] || '3927', 10);
const maxBodyLength = 5 * 1024 * 1024;
const echartsScriptPath = require.resolve('echarts/dist/echarts.min.js');
const echartsGlScriptPath = require.resolve('echarts-gl/dist/echarts-gl.min.js');

let browserPromise = null;

// Small helper for consistent JSON responses from health and error endpoints.
function writeJson(res, statusCode, payload) {
  res.writeHead(statusCode, { 'Content-Type': 'application/json; charset=utf-8' });
  res.end(JSON.stringify(payload));
}

// Read the full request body because the Java side posts one self-contained JSON payload.
function readRequestBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.setEncoding('utf8');

    req.on('data', chunk => {
      body += chunk;
      if (body.length > maxBodyLength) {
        reject(new Error('request body too large'));
      }
    });

    req.on('end', () => {
      resolve(body);
    });

    req.on('error', reject);
  });
}

// Normalize a few common defaults so demo callers can stay concise.
function normalizeOption(payload) {
  const option = payload.option || {};
  if (payload.backgroundColor && !option.backgroundColor) {
    option.backgroundColor = payload.backgroundColor;
  }
  if (typeof option.animation === 'undefined') {
    option.animation = false;
  }
  return option;
}

// The built-in service intentionally renders SVG for 2D charts because it embeds cleanly into iText PDFs.
function renderSvg(payload) {
  const width = Number(payload.width || 720);
  const height = Number(payload.height || 320);
  const chart = echarts.init(null, null, {
    renderer: 'svg',
    ssr: true,
    width: width,
    height: height
  });

  try {
    chart.setOption(normalizeOption(payload), {
      notMerge: true,
      lazyUpdate: false
    });
    return chart.renderToSVGString();
  } finally {
    chart.dispose();
  }
}

function resolveBrowserExecutable() {
  const candidates = [];
  if (process.env.ECHARTS_BROWSER_PATH) {
    candidates.push(process.env.ECHARTS_BROWSER_PATH);
  }
  if (process.platform === 'win32') {
    candidates.push('C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe');
    candidates.push('C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe');
    candidates.push('C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe');
    candidates.push('C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe');
  }
  if (process.platform === 'darwin') {
    candidates.push('/Applications/Google Chrome.app/Contents/MacOS/Google Chrome');
    candidates.push('/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge');
    candidates.push('/Applications/Chromium.app/Contents/MacOS/Chromium');
  }
  if (process.platform === 'linux') {
    candidates.push('/usr/bin/google-chrome');
    candidates.push('/usr/bin/google-chrome-stable');
    candidates.push('/usr/bin/chromium-browser');
    candidates.push('/usr/bin/chromium');
    candidates.push('/snap/bin/chromium');
    candidates.push('/usr/bin/microsoft-edge');
  }

  const existingCandidate = candidates.find(candidate => candidate && fs.existsSync(candidate));
  if (existingCandidate) {
    return existingCandidate;
  }

  return findExecutableInPath([
    'google-chrome',
    'google-chrome-stable',
    'chromium-browser',
    'chromium',
    'microsoft-edge',
    'msedge'
  ]);
}

function findExecutableInPath(names) {
  for (const name of names) {
    try {
      const location = childProcess.execFileSync(process.platform === 'win32' ? 'where' : 'which', [name], {
        stdio: ['ignore', 'pipe', 'ignore'],
        encoding: 'utf8'
      });
      const candidate = location.split(/\r?\n/).map(item => item.trim()).find(Boolean);
      if (candidate && fs.existsSync(candidate)) {
        return candidate;
      }
    } catch (ignored) {
      // Ignore missing executables and keep probing the next candidate.
    }
  }
  return null;
}

function resolveCapabilities() {
  const capabilities = ['svg'];
  if (resolveBrowserExecutable()) {
    capabilities.push('png', 'echarts-gl');
  }
  return capabilities;
}

async function getBrowser() {
  if (!browserPromise) {
    const executablePath = resolveBrowserExecutable();
    if (!executablePath) {
      throw new Error('No browser executable found for PNG rendering. Set ECHARTS_BROWSER_PATH to Edge or Chrome.');
    }
    browserPromise = puppeteer.launch({
      executablePath: executablePath,
      headless: true,
      args: [
        '--no-sandbox',
        '--disable-dev-shm-usage',
        '--enable-webgl',
        '--ignore-gpu-blocklist',
        '--use-angle=swiftshader'
      ]
    });
  }
  return browserPromise;
}

async function renderPng(payload) {
  const width = Number(payload.width || 720);
  const height = Number(payload.height || 320);
  const scale = Number(payload.deviceScaleFactor || 2);
  const option = normalizeOption(payload);
  const browser = await getBrowser();
  const page = await browser.newPage();

  try {
    await page.setViewport({
      width: width,
      height: height,
      deviceScaleFactor: scale
    });
    await page.setContent([
      '<!doctype html>',
      '<html>',
      '<head>',
      '<meta charset="utf-8" />',
      '<style>',
      'html, body { margin: 0; padding: 0; background: transparent; }',
      '#chart { width: ' + width + 'px; height: ' + height + 'px; }',
      '</style>',
      '</head>',
      '<body>',
      '<div id="chart"></div>',
      '</body>',
      '</html>'
    ].join(''));
    await page.addScriptTag({ path: echartsScriptPath });
    await page.addScriptTag({ path: echartsGlScriptPath });
    await page.evaluate((pageOption, chartWidth, chartHeight) => {
      return new Promise((resolve, reject) => {
        const container = document.getElementById('chart');
        const chart = echarts.init(container, null, {
          renderer: 'canvas',
          width: chartWidth,
          height: chartHeight
        });
        const settleAfterFrames = callback => {
          requestAnimationFrame(() => requestAnimationFrame(callback));
        };
        let completed = false;
        let fallbackTimer = null;

        const complete = () => {
          if (completed) {
            return;
          }
          completed = true;
          if (fallbackTimer) {
            clearTimeout(fallbackTimer);
          }
          if (typeof chart.off === 'function') {
            chart.off('finished', handleFinished);
          }
          window.__echartsChart = chart;
          window.__echartsReady = true;
          resolve();
        };

        const handleFinished = () => {
          settleAfterFrames(complete);
        };

        window.__echartsReady = false;
        if (typeof chart.on === 'function') {
          chart.on('finished', handleFinished);
        }
        fallbackTimer = setTimeout(() => {
          settleAfterFrames(complete);
        }, 1500);

        try {
          chart.setOption(pageOption, {
            notMerge: true,
            lazyUpdate: false
          });
        } catch (error) {
          if (fallbackTimer) {
            clearTimeout(fallbackTimer);
          }
          if (typeof chart.off === 'function') {
            chart.off('finished', handleFinished);
          }
          reject(error);
        }
      });
    }, option, width, height);
    const element = await page.$('#chart');
    return await element.screenshot({ type: 'png' });
  } finally {
    try {
      await page.evaluate(() => {
        if (window.__echartsChart) {
          window.__echartsChart.dispose();
          window.__echartsChart = null;
        }
      });
    } catch (ignored) {
      // Ignore cleanup errors because the page is about to close.
    }
    await page.close();
  }
}

async function handleRender(req, res) {
  try {
    const body = await readRequestBody(req);
    const payload = body ? JSON.parse(body) : {};
    const type = String(payload.type || 'svg').toLowerCase();

    if (type === 'svg') {
      const svg = renderSvg(payload);
      res.writeHead(200, {
        'Content-Type': 'image/svg+xml; charset=utf-8'
      });
      res.end(svg);
      return;
    }

    if (type === 'png') {
      const png = await renderPng(payload);
      res.writeHead(200, {
        'Content-Type': 'image/png'
      });
      res.end(png);
      return;
    }

    writeJson(res, 400, {
      error: 'Only svg and png rendering are supported by the built-in local render service.'
    });
  } catch (error) {
    console.error(error && error.stack ? error.stack : error);
    writeJson(res, 500, {
      error: error.message
    });
  }
}

const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url === '/health') {
    writeJson(res, 200, {
      status: 'ok',
      service: serviceName,
      protocolVersion: protocolVersion,
      capabilities: resolveCapabilities(),
      browserAvailable: !!resolveBrowserExecutable()
    });
    return;
  }

  if (req.method === 'POST' && req.url === '/render') {
    handleRender(req, res);
    return;
  }

  writeJson(res, 404, { error: 'Not found' });
});

server.listen(port, host, () => {
  console.log(`echarts-itextpdf render service listening on http://${host}:${port}`);
});

async function shutdown() {
  try {
    if (browserPromise) {
      const browser = await browserPromise;
      await browser.close();
    }
  } catch (ignored) {
    // Ignore browser shutdown errors during process teardown.
  }
  server.close(() => process.exit(0));
}

process.on('SIGINT', () => {
  shutdown();
});

process.on('SIGTERM', () => {
  shutdown();
});
