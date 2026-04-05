package io.github.echartsitext.dsl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HierarchyChartBuilderTest {
    @Test
    void shouldRejectInvalidTreeBuilderArgumentsEarly() {
        TreeChartBuilder builder = Charts.tree();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.node("Operations", 42, null));
        assertThrows(NullPointerException.class, () -> builder.branch("Operations", null));
        assertThrows(NullPointerException.class, () -> builder.series(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidTreemapBuilderArgumentsEarly() {
        TreemapChartBuilder builder = Charts.treemap();

        assertThrows(IllegalArgumentException.class, () -> builder.size(0, 320));
        assertThrows(NullPointerException.class, () -> builder.node("Operations", 42, null));
        assertThrows(NullPointerException.class, () -> builder.branch("Operations", null));
        assertThrows(NullPointerException.class, () -> builder.series(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidSunburstBuilderArgumentsEarly() {
        SunburstChartBuilder builder = Charts.sunburst();

        assertThrows(IllegalArgumentException.class, () -> builder.size(-1, 320));
        assertThrows(NullPointerException.class, () -> builder.node("Portfolio", 42, null));
        assertThrows(NullPointerException.class, () -> builder.branch("Portfolio", null));
        assertThrows(NullPointerException.class, () -> builder.series(null));
        assertThrows(NullPointerException.class, () -> builder.module(null));
        assertThrows(IllegalArgumentException.class, () -> builder.raw(" ", Boolean.TRUE));
    }
}
