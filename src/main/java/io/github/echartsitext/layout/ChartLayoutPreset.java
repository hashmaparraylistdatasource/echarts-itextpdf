package io.github.echartsitext.layout;

import io.github.echartsitext.dsl.CartesianChartBuilder;

import java.util.Objects;

/**
 * Applies a chart-level layout preset to a cartesian chart builder.
 */
public interface ChartLayoutPreset {
    void apply(CartesianChartBuilder builder);

    /**
     * Composes two presets so callers can start from a built-in profile and append a narrow override.
     */
    default ChartLayoutPreset andThen(final ChartLayoutPreset other) {
        Objects.requireNonNull(other, "other");
        return new ChartLayoutPreset() {
            @Override
            public void apply(CartesianChartBuilder builder) {
                ChartLayoutPreset.this.apply(builder);
                other.apply(builder);
            }
        };
    }
}
