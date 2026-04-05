package io.github.echartsitext.pdf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FigureLayoutSupportTest {
    @Test
    void shouldKeepOriginalScaleWhenShrinkToFitHasEnoughRoom() {
        float scale = FigureLayoutSupport.resolveScale(
                FigureLayoutMode.SHRINK_TO_FIT,
                300f,
                150f,
                600f,
                400f
        );

        assertEquals(1f, scale, 0.0001f);
    }

    @Test
    void shouldExpandToAvailableWidthWhenFitWidthHasRoom() {
        float scale = FigureLayoutSupport.resolveScale(
                FigureLayoutMode.FIT_WIDTH,
                300f,
                150f,
                600f,
                400f
        );

        assertEquals(2f, scale, 0.0001f);
    }

    @Test
    void shouldFallBackToHeightConstraintWhenFitWidthWouldOverflowVertically() {
        float scale = FigureLayoutSupport.resolveScale(
                FigureLayoutMode.FIT_WIDTH,
                300f,
                300f,
                600f,
                400f
        );

        assertEquals(1.3333f, scale, 0.0002f);
    }
}
