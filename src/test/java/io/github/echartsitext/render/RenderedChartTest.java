package io.github.echartsitext.render;

import io.github.echartsitext.spec.ChartFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class RenderedChartTest {
    @Test
    void shouldDefensivelyCopyChartBytes() {
        byte[] source = new byte[] {1, 2, 3};
        RenderedChart chart = new RenderedChart(ChartFormat.SVG, source, 320, 160);

        source[0] = 9;
        byte[] firstRead = chart.getBytes();
        firstRead[1] = 8;
        byte[] secondRead = chart.getBytes();

        assertArrayEquals(new byte[] {1, 2, 3}, secondRead);
        assertNotSame(firstRead, secondRead);
    }
}
