package io.github.echartsitext.option;

import io.github.echartsitext.module.ModuleContext;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.theme.ChartTheme;

/**
 * Carries immutable context shared across the composition pipeline.
 */
final class ChartContext implements ModuleContext {
    private final ChartSpec spec;
    private final ChartTheme theme;

    public ChartContext(ChartSpec spec, ChartTheme theme) {
        this.spec = spec;
        this.theme = theme;
    }

    @Override
    public ChartSpec getSpec() {
        return spec;
    }

    @Override
    public ChartTheme getTheme() {
        return theme;
    }
}
