package io.github.echartsitext.render;

import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartSpec;

/**
 * Rendering boundary that turns a chart spec into binary image content.
 */
public interface ChartRenderer {
    /**
     * Renders the given chart in the requested output format.
     */
    RenderedChart render(ChartSpec spec, ChartFormat format);
}
