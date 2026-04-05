package io.github.echartsitext.spec;

/**
 * A simple x/y point used by the fluent DSL and option writer.
 */
public final class ChartPoint {
    private final Double x;
    private final Double y;

    public ChartPoint(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
