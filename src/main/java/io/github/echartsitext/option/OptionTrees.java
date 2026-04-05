package io.github.echartsitext.option;

import io.github.echartsitext.spec.ChartSpec;

import java.util.Map;

/**
 * Internal entry point for turning a chart spec into a composed option tree.
 */
public final class OptionTrees {
    private static final OptionComposer DEFAULT_COMPOSER = new DefaultOptionComposer();

    private OptionTrees() {
    }

    public static Map<String, Object> composeToMap(ChartSpec spec) {
        return DEFAULT_COMPOSER.compose(spec).asMap();
    }
}
