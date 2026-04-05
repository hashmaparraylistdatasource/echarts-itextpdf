package io.github.echartsitext.spec;

/**
 * Output formats supported by the rendering layer.
 */
public enum ChartFormat {
    SVG("svg"),
    PNG("png");

    private final String extension;

    ChartFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
