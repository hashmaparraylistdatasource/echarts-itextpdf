# Contributing

Thanks for considering a contribution to `echarts-itextpdf`.

Please also read:

- `README.md`
- `docs/PROJECT_POSITIONING.md`
- `docs/ARCHITECTURE.md`
- `docs/ROADMAP.md`
- `docs/COMPATIBILITY_MATRIX.md`

## What this project is trying to be

This project aims to be a clean Java library for:

- building typed ECharts options
- rendering charts on the server
- integrating charts into iText PDFs

Please keep changes aligned with that goal.

## Contribution principles

When contributing, prefer:

- typed APIs over raw map-based escape hatches
- explicit layout and rendering strategies over hidden defaults
- small focused classes over large multi-responsibility classes
- tests that prove behavior, not just implementation
- documentation updates when public behavior changes
- stable extension interfaces in `module` over direct dependencies on the internal `option` package

## Local development

### Prerequisites

- Java 8+
- Maven
- Node.js only if you want to run the bundled local render service demos

### Run the test suite

```powershell
cd E:\myApp\cims-export\echarts-itextpdf
mvn test
```

### Run the sample demo

```powershell
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.SamplePdfMain"
```

### Run the gallery demo

```powershell
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.GalleryPdfMain"
```

## Pull request expectations

Please include:

- a short explanation of the user problem being solved
- the architectural reason for the chosen approach
- tests for new public behavior
- README or docs updates when the API changes

## Areas that especially benefit from contributions

- layout presets and collision handling
- more typed ECharts features
- renderer diagnostics and deployment options
- domain extension examples built on top of the core

## Before adding new public API

Ask these questions:

1. Does this solve a real repeated user problem?
2. Can this be modeled as a typed concept instead of raw JSON?
3. Will this still make sense if domain-specific modules are added later?

If the answer to any of these is no, the API probably needs another design pass.
