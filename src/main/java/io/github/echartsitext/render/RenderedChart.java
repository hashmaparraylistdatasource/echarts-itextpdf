package io.github.echartsitext.render;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.spec.ChartFormat;

import java.util.Arrays;
import java.util.Objects;

/**
 * Binary chart payload returned by a renderer, along with the source dimensions.
 */
public final class RenderedChart {
    private final ChartFormat format;
    private final byte[] bytes;
    private final int width;
    private final int height;

    public RenderedChart(ChartFormat format, byte[] bytes, int width, int height) {
        this.format = Objects.requireNonNull(format, "format");
        this.bytes = Arrays.copyOf(Objects.requireNonNull(bytes, "bytes"), bytes.length);
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
    }

    public ChartFormat getFormat() {
        return format;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
