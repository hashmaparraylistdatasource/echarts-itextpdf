package io.github.echartsitext.dsl;

import io.github.echartsitext.spec.ChartPoint3D;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Bar3DChartBuilderTest {
    @Test
    void shouldRejectInvalidBuilderArgumentsEarly() {
        Bar3DChartBuilder builder = Charts.bar3D();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.xAxis3D(null));
        assertThrows(NullPointerException.class, () -> builder.grid3D(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidSeriesInputsEarly() {
        Bar3DChartBuilder builder = Charts.bar3D();

        assertThrows(NullPointerException.class, () -> builder.series("Yield", null));
        assertThrows(NullPointerException.class, () -> builder.series("Yield", Arrays.asList(
                new ChartPoint3D(0, 0, 1),
                null
        )));
        assertThrows(NullPointerException.class,
                () -> builder.series("Yield", Collections.singletonList(new ChartPoint3D(0, 0, 1)), null));
    }
}
