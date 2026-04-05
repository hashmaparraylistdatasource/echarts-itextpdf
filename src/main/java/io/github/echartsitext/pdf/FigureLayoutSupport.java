package io.github.echartsitext.pdf;

/**
 * Small helper that keeps figure scaling rules in one place and makes them easy to test.
 */
final class FigureLayoutSupport {
    private FigureLayoutSupport() {
    }

    static float resolveScale(FigureLayoutMode layoutMode,
                              float naturalWidthPt,
                              float naturalHeightPt,
                              float availableWidthPt,
                              float availableHeightPt) {
        if (naturalWidthPt <= 0f || naturalHeightPt <= 0f || availableWidthPt <= 0f || availableHeightPt <= 0f) {
            return 0f;
        }

        float widthScale = availableWidthPt / naturalWidthPt;
        float heightScale = availableHeightPt / naturalHeightPt;
        float scale;

        if (layoutMode == FigureLayoutMode.FIT_WIDTH) {
            scale = widthScale;
            if (naturalHeightPt * scale > availableHeightPt) {
                scale = heightScale;
            }
        } else {
            scale = Math.min(1f, Math.min(widthScale, heightScale));
        }

        if (scale <= 0f || Float.isNaN(scale) || Float.isInfinite(scale)) {
            return 0f;
        }
        return scale;
    }
}
