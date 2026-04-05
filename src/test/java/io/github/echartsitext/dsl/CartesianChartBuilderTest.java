package io.github.echartsitext.dsl;

import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.TitleSpec;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CartesianChartBuilderTest {
    @Test
    void shouldRejectInvalidBuilderArgumentsEarly() {
        CartesianChartBuilder builder = Charts.line();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.title((Consumer<TitleSpec.Builder>) null));
        assertThrows(NullPointerException.class, () -> builder.legend(null));
        assertThrows(NullPointerException.class, () -> builder.grid(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidSeriesInputsEarly() {
        CartesianChartBuilder builder = Charts.line();

        assertThrows(NullPointerException.class, () -> builder.series("A", null));
        assertThrows(NullPointerException.class, () -> builder.series("A", Arrays.asList(
                new ChartPoint(0d, 1d),
                null
        )));
        assertThrows(NullPointerException.class,
                () -> builder.series("A", Collections.singletonList(new ChartPoint(0d, 1d)), null));
    }
}
