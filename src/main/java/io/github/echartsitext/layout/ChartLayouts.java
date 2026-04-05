package io.github.echartsitext.layout;

import io.github.echartsitext.spec.AxisTitleLayoutMode;

/**
 * Built-in chart layout presets for common report-oriented scenarios.
 */
public final class ChartLayouts {
    private static final ChartLayoutProfile REPORT = ChartLayoutProfile.builder()
            .legendLeft("center")
            .legendTop("bottom")
            .legendOrient("horizontal")
            .gridLeft("10%")
            .gridRight("14%")
            .gridTop("14%")
            .gridBottom("20%")
            .containLabel(Boolean.TRUE)
            .xAxisTitleLayoutMode(AxisTitleLayoutMode.END_SAFE)
            .yAxisTitleLayoutMode(AxisTitleLayoutMode.END_SAFE)
            .build();

    private static final ChartLayoutProfile COMPACT = ChartLayoutProfile.builder()
            .legendLeft("right")
            .legendTop("top")
            .legendOrient("vertical")
            .gridLeft("10%")
            .gridRight("18%")
            .gridTop("16%")
            .gridBottom("16%")
            .containLabel(Boolean.TRUE)
            .xAxisTitleLayoutMode(AxisTitleLayoutMode.MIDDLE_SAFE)
            .yAxisTitleLayoutMode(AxisTitleLayoutMode.MIDDLE_SAFE)
            .build();

    private ChartLayouts() {
    }

    public static ChartLayoutProfile report() {
        return REPORT;
    }

    public static ChartLayoutProfile compact() {
        return COMPACT;
    }

    public static ChartLayoutProfile.Builder builder() {
        return ChartLayoutProfile.builder();
    }
}
