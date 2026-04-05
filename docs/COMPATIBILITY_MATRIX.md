# Compatibility Matrix

This matrix documents what the project is currently designed for and what is verified in CI
or local development.

## Java and Maven

| Component | Requirement | Current validation |
| --- | --- | --- |
| Core library | Java 8+ | CI runs on Java 8 and Java 17 |
| Build tool | Maven 3.8+ recommended | Local development and CI use Maven |

## PDF integration

| Component | Version | Notes |
| --- | --- | --- |
| iText Core | 9.6.0 | Main PDF integration target |
| Java bytecode target | 1.8 | Keeps the library usable in older enterprise runtimes |

## Render service

| Component | Version | Notes |
| --- | --- | --- |
| Node.js | 20 in CI | A modern Node runtime is recommended for local demos |
| Apache ECharts | 5.5.x | Bundled local SSR renderer dependency |
| echarts-gl | 2.0.x | Used for PNG-based 2D/3D rendering paths |
| Browser for PNG | Edge / Chrome / Chromium | Required for `png` and `echarts-gl` rendering |

## Rendering modes

| Mode | Status | Notes |
| --- | --- | --- |
| 2D SVG | Supported | Preferred for iText PDF output |
| 2D PNG | Supported | Uses the local browser-backed render path |
| 3D PNG | Supported | Requires a compatible local browser |

## Operating systems

| OS | Status | Notes |
| --- | --- | --- |
| Windows | Actively exercised in local development | First-class dev environment today |
| Linux | CI covers Java builds and render-service smoke checks | Browser-backed PNG requires an installed browser |
| macOS | Expected to work with a detected local browser | Needs more explicit validation over time |

## Compatibility philosophy

The project prefers:

- a stable Java 8 baseline for library consumers
- explicit validation in CI for the most important runtime combinations
- graceful feature negotiation when optional renderer capabilities are unavailable

If you hit a compatibility issue, please include the Java version, OS, render mode, and
whether the built-in local render service or a remote renderer is being used.
