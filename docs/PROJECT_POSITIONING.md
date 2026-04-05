# Project Positioning

## Who this project is for

`echarts-itextpdf` is primarily for three kinds of users:

1. Java backend engineers who need to generate charts and export PDF reports in server-side systems.
2. Platform or library engineers who want a reusable chart-to-PDF integration layer instead of one-off report code.
3. Domain solution builders who need to build higher-level reporting packages on top of a stable core.

Typical examples include:

- enterprise reporting systems
- quality and laboratory systems
- manufacturing and process analytics
- data products that need scheduled PDF export

This project is **not** primarily for frontend developers, chart designers, or users who only need browser-side ECharts.

## Core user jobs

From the user's perspective, the real jobs are:

1. Turn business data into a chart without hand-writing large ECharts JSON objects.
2. Render that chart reliably on the server.
3. Add the chart into an iText PDF with good layout defaults.
4. Extend the library for domain-specific charts without forking the core.

## What users should not have to think about

Good defaults are a core product requirement.

Users should not need to manually tune:

- SVG sizing
- chart-to-PDF scaling
- axis title collision handling
- local render service startup details
- low-level ECharts option trivia for common cases

If users repeatedly need to tweak these details, the library API is too low-level.

## Product promise

The project should provide:

- a typed Java chart model
- a fluent API for common chart construction
- a pluggable rendering boundary
- an iText adapter that feels native in PDF assembly code
- a stable extension model for domain packages

## Non-goals for the core

The core should avoid becoming:

- a full frontend chart editor
- a report template engine
- a domain-specific library tied to one industry

Those belong in higher-level modules built on top of the core.
