package io.github.echartsitext.dsl;

import io.github.echartsitext.spec.ChartType;

/**
 * Entry point for creating fluent chart builders.
 */
public final class Charts {
    private Charts() {
    }

    public static CartesianChartBuilder line() {
        return new CartesianChartBuilder(ChartType.LINE);
    }

    public static CartesianChartBuilder bar() {
        return new CartesianChartBuilder(ChartType.BAR);
    }

    public static CartesianChartBuilder scatter() {
        return new CartesianChartBuilder(ChartType.SCATTER);
    }

    public static PieChartBuilder pie() {
        return new PieChartBuilder();
    }

    public static RadarChartBuilder radar() {
        return new RadarChartBuilder();
    }

    public static FunnelChartBuilder funnel() {
        return new FunnelChartBuilder();
    }

    public static TreeChartBuilder tree() {
        return new TreeChartBuilder();
    }

    public static TreemapChartBuilder treemap() {
        return new TreemapChartBuilder();
    }

    public static SunburstChartBuilder sunburst() {
        return new SunburstChartBuilder();
    }

    public static BoxplotChartBuilder boxplot() {
        return new BoxplotChartBuilder();
    }

    public static HeatmapChartBuilder heatmap() {
        return new HeatmapChartBuilder();
    }

    public static CandlestickChartBuilder candlestick() {
        return new CandlestickChartBuilder();
    }

    public static Bar3DChartBuilder bar3D() {
        return new Bar3DChartBuilder();
    }
}
