package io.github.echartsitext.dsl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BoxplotChartBuilderTest {
    @Test
    void shouldRejectInvalidBoxplotBuilderArgumentsEarly() {
        BoxplotChartBuilder builder = Charts.boxplot();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.categories(null));
        assertThrows(NullPointerException.class, () -> builder.series("Spread", null));
        assertThrows(NullPointerException.class, () -> builder.series("Spread", Arrays.asList(), null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }
}
