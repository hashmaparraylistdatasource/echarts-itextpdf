package io.github.echartsitext.spec;

/**
 * Supported built-in chart types used by the public DSL.
 */
public enum ChartType {
    LINE("line", false),
    BAR("bar", false),
    SCATTER("scatter", false),
    PIE("pie", false),
    RADAR("radar", false),
    FUNNEL("funnel", false),
    BOXPLOT("boxplot", false),
    HEATMAP("heatmap", false),
    CANDLESTICK("candlestick", false),
    BAR_3D("bar3D", true);

    private final String echartsType;
    private final boolean threeDimensional;

    ChartType(String echartsType, boolean threeDimensional) {
        this.echartsType = echartsType;
        this.threeDimensional = threeDimensional;
    }

    public String echartsType() {
        return echartsType;
    }

    public boolean isThreeDimensional() {
        return threeDimensional;
    }
}
