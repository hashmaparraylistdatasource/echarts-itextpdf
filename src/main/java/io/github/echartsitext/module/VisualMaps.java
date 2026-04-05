package io.github.echartsitext.module;

/**
 * Convenience factory for visualMap modules.
 */
public final class VisualMaps {
    private VisualMaps() {
    }

    public static VisualMapModule.Builder continuous(Number min, Number max) {
        return VisualMapModule.continuous(min, max);
    }

    public static VisualMapModule.Builder piecewise() {
        return VisualMapModule.piecewise();
    }
}
