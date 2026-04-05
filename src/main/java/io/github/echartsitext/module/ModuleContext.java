package io.github.echartsitext.module;

import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.theme.ChartTheme;

/**
 * Stable context exposed to custom modules during option composition.
 */
public interface ModuleContext {
    ChartSpec getSpec();

    ChartTheme getTheme();
}
