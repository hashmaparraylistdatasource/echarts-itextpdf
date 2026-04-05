package io.github.echartsitext.module;

/**
 * Convenience factory for common zoom modules.
 */
public final class Zooms {
    private Zooms() {
    }

    public static OptionModule inside() {
        return DataZoomModule.inside().build();
    }

    public static OptionModule slider() {
        return DataZoomModule.slider().build();
    }
}
