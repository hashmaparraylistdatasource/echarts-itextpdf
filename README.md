# echarts-itextpdf

[![CI](https://github.com/hashmaparraylistdatasource/echarts-itextpdf/actions/workflows/ci.yml/badge.svg)](https://github.com/hashmaparraylistdatasource/echarts-itextpdf/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/hashmaparraylistdatasource/echarts-itextpdf)](https://github.com/hashmaparraylistdatasource/echarts-itextpdf/blob/main/LICENSE)
![Java 8+](https://img.shields.io/badge/Java-8%2B-blue)
![Status](https://img.shields.io/badge/status-public%20alpha-orange)

`echarts-itextpdf` is a Java 8+ library that helps you:

- build ECharts options with a fluent Java DSL
- serialize those options into clean JSON
- render charts through a pluggable renderer
- embed SVG or PNG charts into PDFs with iText 9

## Who this is for

This project is aimed at Java backend developers and library/platform engineers who need reliable server-side chart rendering and PDF export.

Typical users include:

- teams building enterprise reporting systems
- teams generating scheduled or on-demand PDF reports
- teams that want a reusable chart-to-PDF integration layer

More detailed project positioning and architecture notes live in:

- [docs/PROJECT_POSITIONING.md](docs/PROJECT_POSITIONING.md)
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- [docs/ROADMAP.md](docs/ROADMAP.md)
- [docs/COMPATIBILITY_MATRIX.md](docs/COMPATIBILITY_MATRIX.md)
- [CHANGELOG.md](CHANGELOG.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- [SECURITY.md](SECURITY.md)

## Support matrix

First-class typed support:

- line
- bar
- scatter
- pie
- heatmap
- candlestick
- bar3D

Experimental via `raw(...)` escape hatch:

- radar
- boxplot
- graph
- tree
- treemap
- sunburst
- parallel
- sankey
- funnel
- lines / route-like series

Not yet first-class:

- geo / map registration workflows
- higher-level report blocks such as chart + result table composites

## Project maturity

The project is still evolving toward a public GitHub-ready release, but it now includes:

- unit tests
- a bundled local render service for demos
- chart layout presets
- figure layout modes
- CI configuration for Java 8 and Java 17
- issue and pull request templates
- source and Javadoc jar generation during packaging

The project is split into four layers:

1. `spec`: immutable chart model
2. `dsl`: convenient builder API
3. `json` and `render`: ECharts option generation and renderer integration
4. `pdf`: iText-based PDF output

Demo entry points and local demo support code live under [`examples/`](examples/) behind an
explicit Maven profile so the main library artifact stays focused on reusable runtime code.

## Fail-fast validation

The public DSL and adapter entry points now prefer failing fast with clear exceptions instead of
letting invalid state survive until JSON generation, rendering, or PDF writing.

That includes checks for:

- non-positive chart sizes
- blank raw option keys
- negative line widths or axis indexes
- null renderer / figure / chart inputs
- invalid HTTP renderer endpoints and timeouts

The immutable `spec` package is also intentionally biased toward builders and static factories,
so long parameter constructors are kept out of the main supported API surface where possible.
Three-dimensional charts now also live in the main immutable model instead of being treated as
raw extension blobs inside the DSL.
The built-in modules follow the same rule now, so invalid zoom/visualMap/annotation arguments fail
early and nested module payloads are defensively copied before they enter the chart pipeline.

## First-class chart types

Current typed DSL coverage includes:

- `Charts.line()`
- `Charts.bar()`
- `Charts.scatter()`
- `Charts.pie()`
- `Charts.heatmap()`
- `Charts.candlestick()`
- `Charts.bar3D()`

That means these chart families now have dedicated builders and immutable typed specs instead of
relying on `raw(...)` option fragments.

## Typed 3D builder

Common 3D `bar3D` charts no longer need to be assembled through large raw option maps.

```java
ChartSpec chart = Charts.bar3D()
        .title("3D Bar Chart")
        .xAxis3D(axis -> axis.name("Temperature").categories(Arrays.asList("50C", "60C", "70C")))
        .yAxis3D(axis -> axis.name("Flow Rate").categories(Arrays.asList("0.8", "1.0", "1.2")))
        .zAxis3D(axis -> axis.name("Yield"))
        .series("Yield Surface", Arrays.asList(
                new ChartPoint3D(0, 0, 4.1d),
                new ChartPoint3D(1, 1, 7.3d)
        ))
        .build();
```

Typed 3D charts also participate in `EchartsFigure.autoFormat()`, so 3D chart types default to
PNG output without relying only on raw option sniffing.

The same first-class approach now also covers several high-value 2D families that are awkward to
model through a generic x/y point API alone:

```java
ChartSpec pie = Charts.pie()
        .title("Channel Mix")
        .donut("35%", "68%")
        .slice("Automated", 48)
        .slice("Scheduled", 27)
        .slice("Manual", 15)
        .build();

ChartSpec heatmap = Charts.heatmap()
        .title("Heatmap")
        .xCategories(Arrays.asList("Mon", "Tue", "Wed"))
        .yCategories(Arrays.asList("Morning", "Noon"))
        .series("Load", Arrays.asList(
                new HeatmapPoint(0, 0, 11),
                new HeatmapPoint(1, 1, 23)
        ))
        .build();

ChartSpec candlestick = Charts.candlestick()
        .title("Price")
        .categories(Arrays.asList("04-01", "04-02", "04-03"))
        .series("Price", Arrays.asList(
                new CandlestickValue(20, 24, 18, 26),
                new CandlestickValue(24, 22, 21, 25)
        ))
        .build();
```

## Layout strategies

Axis title placement is exposed as a high-level layout choice instead of forcing callers to tune
`nameLocation`, `nameGap`, and grid margins by hand.

For example:

```java
ChartSpec chart = Charts.line()
        .xAxis(axis -> axis
                .name("Time (min)")
                .titleLayoutMode(AxisTitleLayoutMode.END_SAFE))
        .yAxis(axis -> axis
                .name("mAU")
                .titleLayoutMode(AxisTitleLayoutMode.END_SAFE))
        .build();
```

Current built-in modes include:

- `END_SAFE`: keep titles near axis endpoints while reserving space for edge labels
- `MIDDLE_SAFE`: use a conservative center-oriented title layout

Chart-level presets are also available for common report styles:

```java
ChartSpec chart = Charts.line()
        .layout(ChartLayouts.report())
        .xAxis(axis -> axis.name("Time (min)"))
        .yAxis(axis -> axis.name("mAU"))
        .build();
```

Current built-in presets include:

- `ChartLayouts.report()`
- `ChartLayouts.compact()`

Built-in profiles are now incrementally customizable, so teams can define one shared base and
only override the pieces that differ:

```java
ChartLayoutProfile qcReport = ChartLayouts.report().toBuilder()
        .legendTop("top")
        .gridRight("20%")
        .build();

ChartSpec chart = Charts.line()
        .layout(qcReport)
        .xAxis(axis -> axis.name("Time (min)"))
        .yAxis(axis -> axis.name("mAU"))
        .build();
```

These layout defaults now also apply to additional axes that are added later with
`addXAxis(...)` and `addYAxis(...)`, so dual-axis charts keep one consistent layout profile.

## Quick start

Add the dependency:

```xml
<dependency>
    <groupId>io.github.echartsitext</groupId>
    <artifactId>echarts-itextpdf</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

```java
ChartSpec chart = Charts.line()
        .size(720, 320)
        .title("Sample Chromatogram")
        .xAxis(axis -> axis.name("Time (min)").range(0d, 60d).splitNumber(10))
        .yAxis(axis -> axis.name("mAU").autoRange())
        .series("Sample A", Arrays.asList(
                new ChartPoint(0d, 0d),
                new ChartPoint(10d, 12.4d),
                new ChartPoint(20d, 8.1d)
        ), series -> series.smooth(true).lineWidth(1.4))
        .build();

String optionJson = new JacksonEchartsOptionWriter().write(chart);
```

Themes are also incrementally customizable, which is much easier to maintain than repeating a
long constructor:

```java
ChartTheme teamTheme = ChartTheme.report().toBuilder()
        .defaultFontSize(13)
        .palette("#005f73", "#9b2226", "#0a9396")
        .build();

ChartSpec chart = Charts.line()
        .theme(teamTheme)
        .xAxis(axis -> axis.name("Time (min)"))
        .yAxis(axis -> axis.name("mAU"))
        .build();
```

## Built-in modules

Common cross-cutting ECharts features can be added as modules instead of raw JSON:

```java
ChartSpec chart = Charts.line()
        .series("A", points)
        .module(Zooms.inside())
        .module(Annotations.horizontalLine("Threshold", 8.5d))
        .module(VisualMaps.continuous(0d, 20d)
                .dimension(1)
                .seriesIndex(0)
                .inRange("color", Arrays.asList("#006e54", "#bb1005"))
                .build())
        .build();
```

Current built-in modules include:

- `Zooms`
- `Annotations`
- `VisualMaps`

Custom modules should implement `OptionModule` against the stable `OptionTarget` and
`ModuleContext` interfaces in the `module` package. The lower-level `option` package is now
treated as internal composition infrastructure.

`OptionTargets` is available as a helper for common nested-map and nested-list mutations inside
custom modules.

## PDF output

```java
ChartRenderer renderer = new HttpChartRenderer(URI.create("http://localhost:8080/render"));
EchartsFigure figure = EchartsFigure.of(chart)
        .renderer(renderer)
        .autoFormat()
        .fitWidth()
        .caption("Figure 1. Demo chart")
        .build();

byte[] pdf = new PdfChartWriter().writeSinglePage(figure);
```

You can also treat charts as document elements and add them directly to an existing iText `Document`:

```java
PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
Document document = new Document(pdfDocument);

EchartsFigure figure = EchartsFigure.of(chart)
        .renderer(renderer)
        .autoFormat()
        .caption("Figure 1. Demo chart")
        .build();

figure.addTo(document);
document.close();
```

Figure layout is also explicit. Current built-in modes include:

- `shrinkToFit()`: keep the original size when possible and only shrink when required
- `fitWidth()`: prefer filling the available width and fall back to height constraints if needed

## End-to-End Local Rendering

This project now includes a built-in local render service under `render-service/`.
It uses the official Apache ECharts SSR SVG flow and exposes:

- `GET /health`
- `POST /render`

The built-in local service supports:

- `svg` via Apache ECharts SSR for crisp 2D PDF output
- `png` via a local browser for 2D/3D charts that need canvas or WebGL

You can run the service by itself:

```powershell
cd render-service
npm install
npm start
```

Or let the Java demo auto-install dependencies, auto-start the service, and generate a PDF in one command:

```powershell
cd E:\myApp\cims-export\echarts-itextpdf
$env:JAVA_HOME=$env:JAVA8_HOME
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.SamplePdfMain"
```

To generate a gallery PDF with line, pie, scatter, heatmap, candlestick, and 3D charts:

```powershell
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.GalleryPdfMain"
```

The gallery now performs an upfront capability check for `svg`, `png`, and `echarts-gl`.
If the local machine does not have a usable browser for PNG/WebGL rendering, the example fails
early with a clear message instead of getting deep into PDF generation first.

If the default demo port is already occupied, the local service now falls back to a free port automatically and the Java side uses the resolved endpoint.

Each `LocalNodeRenderService` instance manages its own local service lifecycle. This keeps concurrent demos and tests from interfering with each other when multiple JVM processes run at the same time.

PNG rendering now waits for the browser-side chart to finish rendering instead of relying on a
fixed sleep, which makes 3D screenshots much more stable on slower machines and CI agents.

If npm registry access is slow in your environment, you can override it before running:

```powershell
$env:ECHARTS_RENDER_NPM_REGISTRY="https://registry.npmmirror.com"
```

By default the demo writes the file to:

`examples/output/sample-chart.pdf`

You can also override the output path:

```powershell
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.SamplePdfMain" "-Dexec.args=examples/output/custom-demo.pdf"
```

## Escape hatch

When ECharts has a field that is not modeled yet, you can still push raw option fragments:

```java
ChartSpec chart = Charts.line()
        .series("A", points)
        .raw("dataZoom", Arrays.<Object>asList(Collections.singletonMap("type", "inside")))
        .build();
```

## Rendering strategy

This repository provides:

- a renderer contract: `ChartRenderer`
- a generic HTTP implementation: `HttpChartRenderer`
- a PDF adapter: `PdfChartWriter`

The demo-only helper `LocalNodeRenderService` now lives under the examples support code rather
than the main runtime artifact.

The HTTP renderer expects a self-hosted chart rendering service that accepts:

```json
{
  "option": { "...": "..." },
  "width": 720,
  "height": 320,
  "type": "svg",
  "backgroundColor": "#ffffff"
}
```

When rendering fails, `HttpChartRenderer` now throws `ChartRenderException` with structured
diagnostics such as:

- `RenderFailureKind`
- renderer endpoint
- HTTP status code when available
- response body summary for service-side failures
- requested format and chart dimensions

That makes production logging and GitHub issue triage much easier than a generic transport error.

## API boundary notes

The intended public extension surface is:

- `dsl` for chart construction
- `spec` for immutable chart descriptions
- `module` for reusable cross-cutting ECharts extensions
- `json` for writing option trees or JSON
- `render` for renderer integration
- `pdf` for iText-facing usage

The `option` package is implementation detail. It may evolve more aggressively as the project
continues tightening its internal architecture.

## iText version

This project is wired for **iText Core 9.6.0** and compiled for **Java 8**.

## Packaging notes

A normal Maven package build now produces:

- the main library jar
- a `-sources` jar
- a `-javadoc` jar

The main jar also declares `Automatic-Module-Name: io.github.echartsitext` for stable JPMS
module naming in downstream builds.

## License note

iText is distributed under AGPL/commercial terms.
If you publish this project as open source and rely on the community edition, AGPL-compatible distribution is the safest default.
