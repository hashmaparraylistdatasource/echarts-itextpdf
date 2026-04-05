package io.github.echartsitext.module;

/**
 * Convenience factory for annotation modules backed by ECharts markLine.
 */
public final class Annotations {
    private Annotations() {
    }

    public static OptionModule horizontalLine(String name, Number yAxis) {
        return horizontalLine(0, name, yAxis);
    }

    public static OptionModule horizontalLine(int seriesIndex, String name, Number yAxis) {
        return SeriesMarkLineModule.horizontalLine(seriesIndex, name, yAxis);
    }

    public static OptionModule verticalLine(String name, Number xAxis) {
        return verticalLine(0, name, xAxis);
    }

    public static OptionModule verticalLine(int seriesIndex, String name, Number xAxis) {
        return SeriesMarkLineModule.verticalLine(seriesIndex, name, xAxis);
    }
}
