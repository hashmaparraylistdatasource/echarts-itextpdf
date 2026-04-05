# Roadmap

## Goal

Make `echarts-itextpdf` a publishable GitHub open-source project with:

- elegant code
- strong defaults
- reliable rendering
- clear extension points
- documentation that explains how to use and extend it

## Release phases

### Phase 1: Product clarity

Objective:
Define what the project is and what it is not.

Tasks:

- document target users
- document core architecture
- document extension philosophy
- document current limitations

Exit criteria:

- contributors can understand the repo without reading the whole codebase

### Phase 2: API stabilization

Objective:
Make the Java API predictable and pleasant for real users.

Tasks:

- introduce explicit layout presets
- reduce accidental low-level configuration leakage
- clean up naming and package boundaries
- decide what is public API versus internal implementation

Exit criteria:

- common charts can be built without raw option fragments

### Phase 3: Rendering robustness

Objective:
Make rendering behavior reliable and diagnosable.

Tasks:

- strengthen renderer health checks and capability negotiation
- improve error reporting around render failures
- validate SVG and PNG output paths separately
- document supported deployment models

Exit criteria:

- users can understand and fix renderer failures quickly

### Phase 4: PDF ergonomics

Objective:
Make chart usage inside iText feel native and stable.

Tasks:

- formalize figure layout strategies
- improve auto-scaling and collision handling
- test multi-page and mixed-format figures
- define clear behavior for captions, legends, and large charts

Exit criteria:

- `document.add(figure)` works predictably across common report layouts

### Phase 5: Open-source readiness

Objective:
Prepare the repository for public collaboration.

Tasks:

- add LICENSE
- add CONTRIBUTING
- add CI
- add issue templates
- add release notes structure
- add compatibility matrix

Exit criteria:

- repository is ready for public publishing and outside contributions

## Current priority backlog

1. Extract layout behavior into explicit strategies instead of hidden defaults.
2. Separate examples and demo concerns from the main library surface.
3. Improve chart collision handling for endpoint axis titles and similar layout edge cases.
4. Clarify and simplify the core extension model.
5. Add repository-level open-source metadata and contribution guidance.

## Iteration rule

Every iteration should follow the same loop:

1. identify a real user-facing problem
2. isolate the root cause
3. implement the smallest clean fix
4. add tests or documentation that keep it fixed
5. reassess whether the architecture still fits the goal
