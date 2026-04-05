package io.github.echartsitext.spec;

/**
 * A simple x/y/z point used by typed 3D chart builders.
 */
public final class ChartPoint3D {
    private final Number x;
    private final Number y;
    private final Number z;

    public ChartPoint3D(Number x, Number y, Number z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Number getX() {
        return x;
    }

    public Number getY() {
        return y;
    }

    public Number getZ() {
        return z;
    }
}
