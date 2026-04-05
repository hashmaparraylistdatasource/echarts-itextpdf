package io.github.echartsitext.example.support;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Starts and manages the bundled local Node-based ECharts SSR service used by demos.
 */
public final class LocalNodeRenderService implements Closeable {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String EXPECTED_SERVICE_NAME = "echarts-itextpdf-render-service";
    private static final int EXPECTED_PROTOCOL_VERSION = 2;
    private static final String SERVICE_DIRECTORY = "render-service";
    private static final String HEALTH_PATH = "/health";
    private static final String RENDER_PATH = "/render";
    private static final String LOG_DIRECTORY = ".logs";
    private static final String LOG_FILE_PREFIX = "render-service-";
    private static final String LOCK_FILE = ".service-start.lock";
    private static final String SERVICE_SCRIPT = "server.js";
    private static final String NPM_REGISTRY_ENV = "ECHARTS_RENDER_NPM_REGISTRY";
    private static final String NPM_REGISTRY_FALLBACK = "https://registry.npmmirror.com";
    private static final List<String> NODE_MODULE_MARKERS = Arrays.asList(
            "node_modules/echarts/package.json",
            "node_modules/echarts-gl/package.json",
            "node_modules/puppeteer-core/package.json"
    );

    private final Path projectRoot;
    private final Path serviceDirectory;
    private final int preferredPort;
    private int activePort;
    private URI healthEndpoint;
    private URI renderEndpoint;
    private Process process;
    private boolean startedByThisInstance;

    public LocalNodeRenderService(Path projectRoot) {
        this(projectRoot, 3927);
    }

    public LocalNodeRenderService(Path projectRoot, int port) {
        this.projectRoot = projectRoot.toAbsolutePath().normalize();
        this.serviceDirectory = this.projectRoot.resolve(SERVICE_DIRECTORY);
        this.preferredPort = port;
        bindToPort(port);
    }

    public URI getRenderEndpoint() {
        return renderEndpoint;
    }

    public int getPort() {
        return activePort;
    }

    public Path getServiceDirectory() {
        return serviceDirectory;
    }

    public Path getLogFile() {
        int portForLog = activePort > 0 ? activePort : preferredPort;
        return serviceDirectory.resolve(LOG_DIRECTORY).resolve(LOG_FILE_PREFIX + portForLog + ".log");
    }

    public void start() {
        ServiceHealth currentHealth = readHealth(healthEndpoint);
        if (startedByThisInstance && currentHealth != null && isCompatible(currentHealth)) {
            return;
        }
        ensureServiceFilesExist();
        acquireStartupLockAndStart();
    }

    public boolean isHealthy() {
        ServiceHealth health = readHealth(healthEndpoint);
        return health != null && "ok".equalsIgnoreCase(health.status);
    }

    public void requireCapabilities(String... requiredCapabilities) {
        ServiceHealth health = readHealth(healthEndpoint);
        if (!isCompatible(health, requiredCapabilities)) {
            throw new IllegalStateException("Local render service does not provide required capabilities "
                    + Arrays.asList(requiredCapabilities) + ". Current health: "
                    + (health == null ? "unavailable" : describe(health)));
        }
    }

    private void ensureServiceFilesExist() {
        if (!Files.exists(serviceDirectory.resolve(SERVICE_SCRIPT))) {
            throw new IllegalStateException("Missing local render service script: " + serviceDirectory.resolve(SERVICE_SCRIPT));
        }
        if (!Files.exists(serviceDirectory.resolve("package.json"))) {
            throw new IllegalStateException("Missing local render service package.json: " + serviceDirectory.resolve("package.json"));
        }
    }

    private void ensureDependenciesInstalled() {
        if (hasAllDependenciesInstalled()) {
            return;
        }
        ensureLogDirectory();
        List<String> installCommand = Arrays.asList(resolveNpmCommand(), "install", "--no-fund", "--no-audit");
        String customRegistry = System.getenv(NPM_REGISTRY_ENV);
        if (customRegistry != null && customRegistry.trim().length() > 0) {
            runCommand(withRegistry(installCommand, customRegistry.trim()), serviceDirectory, 180_000L);
            return;
        }
        try {
            runCommand(installCommand, serviceDirectory, 180_000L);
        } catch (IllegalStateException ex) {
            // A fallback mirror keeps the demo usable on networks where the default registry is unstable.
            runCommand(withRegistry(installCommand, NPM_REGISTRY_FALLBACK), serviceDirectory, 180_000L);
        }
    }

    private boolean hasAllDependenciesInstalled() {
        for (String marker : NODE_MODULE_MARKERS) {
            if (!Files.exists(serviceDirectory.resolve(Paths.get(marker)))) {
                return false;
            }
        }
        return true;
    }

    private void startProcess(int port) {
        try {
            ensureLogDirectory();
            ProcessBuilder processBuilder = new ProcessBuilder(resolveNodeCommand(), SERVICE_SCRIPT, String.valueOf(port));
            processBuilder.directory(serviceDirectory.toFile());
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(getLogFile().toFile()));
            this.process = processBuilder.start();
            this.startedByThisInstance = true;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start local render service", e);
        }
    }

    private void waitUntilHealthy(long timeoutMillis, int port) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        URI endpoint = healthEndpointFor(port);
        while (System.currentTimeMillis() < deadline) {
            ServiceHealth health = readHealth(endpoint);
            if (health != null && isCompatible(health)) {
                bindToPort(port);
                return;
            }
            if (process != null && !process.isAlive()) {
                throw new IllegalStateException("Local render service exited unexpectedly. See log: " + getLogFile());
            }
            sleep(300L);
        }
        ServiceHealth health = readHealth(endpoint);
        if (health != null) {
            throw new IllegalStateException("Local render service responded with incompatible health info: "
                    + describe(health) + ". See log: " + getLogFile());
        }
        throw new IllegalStateException("Local render service did not become healthy in time. See log: " + getLogFile());
    }

    private ServiceHealth readHealth(URI endpoint) {
        HttpURLConnection connection = null;
        try {
            URL url = endpoint.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1_000);
            connection.setReadTimeout(1_000);
            if (connection.getResponseCode() != 200) {
                return null;
            }
            return parseHealthPayload(readAll(connection));
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private int selectStartupPort() {
        if (preferredPort <= 0) {
            return findFreePort();
        }
        if (canBindPort(preferredPort)) {
            return preferredPort;
        }
        return findFreePort();
    }

    private void bindToPort(int port) {
        this.activePort = port;
        this.healthEndpoint = healthEndpointFor(port);
        this.renderEndpoint = renderEndpointFor(port);
    }

    private URI healthEndpointFor(int port) {
        return URI.create("http://127.0.0.1:" + port + HEALTH_PATH);
    }

    private URI renderEndpointFor(int port) {
        return URI.create("http://127.0.0.1:" + port + RENDER_PATH);
    }

    private void ensureLogDirectory() {
        try {
            Files.createDirectories(serviceDirectory.resolve(LOG_DIRECTORY));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create render service log directory", e);
        }
    }

    private void acquireStartupLockAndStart() {
        ensureLogDirectory();
        Path lockPath = serviceDirectory.resolve(LOG_DIRECTORY).resolve(LOCK_FILE);
        try (FileChannel channel = FileChannel.open(lockPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock lock = channel.lock()) {
            ServiceHealth existingHealth = readHealth(healthEndpoint);
            if (existingHealth != null && isCompatible(existingHealth)) {
                return;
            }
            // Install dependencies under the same lock so first-run demos do not race on node_modules.
            ensureDependenciesInstalled();
            ServiceHealth refreshedHealth = readHealth(healthEndpoint);
            if (refreshedHealth != null && isCompatible(refreshedHealth)) {
                return;
            }
            int startPort = selectStartupPort();
            bindToPort(startPort);
            startProcess(startPort);
            waitUntilHealthy(30_000L, startPort);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to coordinate local render service startup", e);
        }
    }

    private void runCommand(List<String> command, Path workDirectory, long timeoutMillis) {
        Process localProcess = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(workDirectory.toFile());
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(getLogFile().toFile()));
            localProcess = processBuilder.start();
            if (!localProcess.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                localProcess.destroyForcibly();
                throw new IllegalStateException("Command timed out: " + command);
            }
            if (localProcess.exitValue() != 0) {
                throw new IllegalStateException("Command failed: " + command + ". See log: " + getLogFile());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to run command: " + command, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Command interrupted: " + command, e);
        } finally {
            if (localProcess != null && localProcess.isAlive()) {
                localProcess.destroyForcibly();
            }
        }
    }

    private List<String> withRegistry(List<String> command, String registry) {
        List<String> commandWithRegistry = new ArrayList<String>(command);
        commandWithRegistry.add("--registry=" + registry);
        return commandWithRegistry;
    }

    private static String readAll(HttpURLConnection connection) throws IOException {
        java.io.InputStream inputStream = connection.getInputStream();
        try {
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toString("UTF-8");
        } finally {
            inputStream.close();
        }
    }

    static ServiceHealth parseHealthPayload(String payload) throws IOException {
        return OBJECT_MAPPER.readValue(payload, ServiceHealth.class);
    }

    static boolean isCompatible(ServiceHealth health) {
        return isCompatible(health, "svg");
    }

    static boolean isCompatible(ServiceHealth health, String... requiredCapabilities) {
        return health != null
                && "ok".equalsIgnoreCase(health.status)
                && EXPECTED_SERVICE_NAME.equals(health.service)
                && health.protocolVersion == EXPECTED_PROTOCOL_VERSION
                && health.capabilities != null
                && health.capabilities.containsAll(Arrays.asList(requiredCapabilities));
    }

    static boolean canBindPort(int port) {
        if (port <= 0) {
            return false;
        }
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Ignore close failures for availability checks.
                }
            }
        }
    }

    static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to locate a free port for the local render service", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Ignore close failures for availability checks.
                }
            }
        }
    }

    private static String describe(ServiceHealth health) {
        return "service=" + health.service
                + ", protocolVersion=" + health.protocolVersion
                + ", capabilities=" + health.capabilities;
    }

    private String resolveNodeCommand() {
        return isWindows() ? "node.exe" : "node";
    }

    private String resolveNpmCommand() {
        return isWindows() ? "npm.cmd" : "npm";
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for local render service", e);
        }
    }

    @Override
    public void close() {
        if (startedByThisInstance && process != null && process.isAlive()) {
            // Only stop the process when this instance was responsible for starting it.
            process.destroy();
            sleep(500L);
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    static final class ServiceHealth {
        public String status;
        public String service;
        public int protocolVersion;
        public List<String> capabilities;
        public boolean browserAvailable;
    }
}
