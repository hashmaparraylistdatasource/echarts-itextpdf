# Architecture

## First-principles view

The project exists to solve one server-side problem:

`business intent -> chart -> rendered asset -> PDF element`

That flow leads to four clear architectural layers:

1. `core-model`
   Purpose: represent the chart as a typed Java object graph.

2. `option-composition`
   Purpose: transform the typed model into valid ECharts option trees.

3. `rendering`
   Purpose: turn options into SVG or PNG through a pluggable renderer.

4. `pdf-adapter`
   Purpose: expose charts as iText-friendly PDF elements.

Across those layers, configuration should stay split by responsibility:

- `theme`
  Purpose: visual defaults such as font family, palette, line width, text color, and fallback spacing.
- `layout`
  Purpose: placement strategy such as legend position, grid margins, and axis title layout mode.

That distinction matters for maintainability. A team should be able to change a visual identity
without rewriting report spacing rules, and change a report layout profile without cloning a whole
theme constructor.

## Current codebase strengths

The current repository already has the correct broad direction:

- immutable chart specs
- a fluent DSL
- a renderer abstraction
- a native-feeling `EchartsFigure`

This is a good foundation.

## Current architectural mismatches

The current repository still mixes product layers that should be more clearly separated:

1. Demo entry points still live in the same Maven module, even though they no longer live in the main runtime source tree.
2. The bundled Node render service is treated as a local convenience, but for open-source it is effectively a separate product surface.
3. Some layout and theme defaults still overlap more than they should.
4. The core API is still too close to ECharts vocabulary in some places.

## Target architecture for open-source evolution

The long-term target should be a multi-module repository:

```text
echarts-itextpdf/
  docs/
  echarts-itextpdf-core/
  echarts-itextpdf-renderer-api/
  echarts-itextpdf-renderer-http/
  echarts-itextpdf-itext/
  echarts-itextpdf-render-service-node/
  echarts-itextpdf-examples/
```

## Why multi-module matters

This split improves:

- release clarity
- dependency isolation
- testability
- contributor comprehension
- future domain extensions

For example:

- users who only want typed option generation should not pull in iText
- users who already have a render service should not need local Node tooling
- domain packages should depend on `core` and `itext`, not on demos

## Near-term architectural priorities

The next iterations should focus on:

1. stabilizing the public API
2. turning layout behavior into explicit strategies
3. separating examples and demo tooling from the main library surface
4. strengthening renderer contracts and diagnostics

That second priority is now moving in the right direction:

- layout presets are now real `ChartLayoutProfile` objects that can be cloned and overridden
- themes can now be cloned and incrementally customized with `toBuilder()`
- grid fallback now honors theme defaults more consistently when fluent builders leave a side unset

The first priority is now partly addressed: demos and their local support code have moved under
`examples/` with an explicit profile, but the longer-term direction should still be a dedicated
examples module. The current build also explicitly excludes example packages from the default
test and jar outputs so profile-specific compiled classes do not leak back into the core artifact.

The public API is also getting stricter about predictable behavior:

- layout defaults now propagate to additional axes instead of only the primary axis
- render payload objects are moving toward true immutability via defensive copies
- example-side PNG and 3D rendering now fail earlier when required capabilities are missing

## Extension philosophy

Extension should happen by adding higher-level modules, not by leaking more raw JSON.

The core should expose:

- typed specs
- modules
- presets
- adapters

Domain packages should build on these instead of patching internals.
