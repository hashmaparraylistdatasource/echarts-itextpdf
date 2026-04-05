package io.github.echartsitext.spec;

/**
 * A typed heatmap cell location and value.
 */
public final class HeatmapPoint {
    private final Number x;
    private final Number y;
    private final Number value;

    public HeatmapPoint(Number x, Number y, Number value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Number getX() {
        return x;
    }

    public Number getY() {
        return y;
    }

    public Number getValue() {
        return value;
    }
}
