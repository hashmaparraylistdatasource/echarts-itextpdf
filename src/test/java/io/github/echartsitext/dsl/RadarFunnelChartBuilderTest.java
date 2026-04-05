package io.github.echartsitext.dsl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RadarFunnelChartBuilderTest {
    @Test
    void shouldRejectInvalidRadarBuilderArgumentsEarly() {
        RadarChartBuilder builder = Charts.radar();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.indicator("Purity", 0d, 100d, null));
        assertThrows(NullPointerException.class, () -> builder.value("Batch A", null));
        assertThrows(NullPointerException.class, () -> builder.value("Batch A", Arrays.asList(80d, 92d), null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidFunnelBuilderArgumentsEarly() {
        FunnelChartBuilder builder = Charts.funnel();

        assertThrows(IllegalArgumentException.class, () -> builder.size(-1, 320));
        assertThrows(NullPointerException.class, () -> builder.slice("Qualified", 80, null));
        assertThrows(NullPointerException.class, () -> builder.series(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }
}
