package io.github.echartsitext.spec;

/**
 * High-level layout presets for axis titles.
 * These presets encode safe defaults so users do not need to hand-tune
 * low-level ECharts spacing options for common report scenarios.
 */
public enum AxisTitleLayoutMode {
    /**
     * Put the title at the axis endpoint while reserving space for edge labels.
     */
    END_SAFE,
    /**
     * Put the title around the center of the axis for the most conservative layout.
     */
    MIDDLE_SAFE
}
