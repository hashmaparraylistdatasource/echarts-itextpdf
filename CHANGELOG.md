# Changelog

All notable changes to `echarts-itextpdf` should be documented in this file.

The format is inspired by [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and the project intends to follow semantic versioning once the first public release is cut.

## [Unreleased]

### Added

- Typed chart DSL and immutable chart spec model
- Public repository badges and a clearer README support matrix
- Option composition layer with typed modules and layout presets
- Incrementally customizable `ChartLayoutProfile` objects with `toBuilder()` support
- Native-feeling iText `EchartsFigure` support
- Built-in local Node render service for demos and smoke testing
- End-to-end sample and gallery demos
- Structured renderer diagnostics via `ChartRenderException` and `RenderFailureKind`
- Stable custom-module extension interfaces via `OptionTarget` and `ModuleContext`
- `OptionTargets` helpers for writing custom modules without repetitive nested-map boilerplate
- GitHub CI, issue templates, pull request template, contributing guide, code of conduct,
  security policy, compatibility matrix, and AGPL licensing metadata
- packaging now attaches source and Javadoc jars and declares a stable `Automatic-Module-Name`
- package-level Javadocs were added for the main public API packages
- Incrementally customizable `ChartTheme` builder support
- Defensive-copy tests for `ChartTheme` and `RenderedChart`
- Fail-fast validation tests for the public DSL, spec model, renderer, and PDF entry points
- First-class typed `Charts.bar3D()` support with 3D axis, grid, and point models
- First-class typed `Charts.pie()`, `Charts.heatmap()`, and `Charts.candlestick()` support
- First-class typed `Charts.radar()` and `Charts.funnel()` support
- First-class typed `Charts.boxplot()` support
- First-class typed `Charts.tree()`, `Charts.treemap()`, and `Charts.sunburst()` support

### Changed

- Axis title placement moved toward explicit layout strategies instead of hidden tuning
- Figure sizing moved toward explicit layout modes instead of implicit scaling behavior
- Theme grid fallback now works consistently even when charts are assembled through the fluent DSL
- Layout profiles now apply axis-title defaults to additional axes as well as primary axes
- Local render service lifecycle made safer for concurrent demos and tests
- Gallery examples now preflight required `png` / `echarts-gl` capabilities instead of failing late
- Browser-side PNG rendering no longer relies on a fixed screenshot delay
- `HttpChartRenderer` now reports actionable endpoint, status, and response details instead of a generic transport failure
- Demo entry points moved out of the main runtime source tree into `examples/` with an explicit Maven profile
- `option` package was pushed further behind the public API so custom modules no longer depend on composition internals
- `LocalNodeRenderService` moved out of the main runtime artifact into example support code
- Default test and jar outputs now exclude example packages so profile-only compiled classes do not leak into the core build
- Renderer transport tests now tolerate environment-specific timeout vs connection-refused behavior
- Public builders and adapters now reject invalid dimensions, blank raw keys, null critical inputs, and negative numeric values earlier
- First-run example service startup now performs dependency installation under the same startup lock used for process launch
- Long parameter constructors in the immutable `spec` model were narrowed behind builders/static factories to keep the supported API surface smaller
- Gallery examples now use the typed `bar3D` DSL instead of assembling the 3D chart through a large raw option block
- Typed 3D chart structures now live inside `ChartSpec` instead of only being forwarded as raw extensions
- `ChartSpec` now rejects mixed 2D/3D model shapes so invalid chart definitions fail early
- `EchartsFigure.autoFormat()` now treats typed 3D chart types as PNG-first by design
- Built-in modules now validate indices/ranges more aggressively and defensively copy nested payload values
- Gallery and README examples now cover pie, radar, funnel, tree, treemap, sunburst, boxplot, heatmap, and candlestick charts in addition to cartesian and 3D samples
- Maven project metadata now includes GitHub URL, SCM, issue tracker, and developer information for downstream consumers
