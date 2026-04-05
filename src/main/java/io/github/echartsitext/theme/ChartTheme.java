package io.github.echartsitext.theme;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Collects reusable defaults for fonts, palette, and fallback grid spacing.
 */
public final class ChartTheme {
    private final String fontFamily;
    private final int defaultFontSize;
    private final double defaultLineWidth;
    private final String axisColor;
    private final String textColor;
    private final String backgroundColor;
    private final List<String> palette;
    private final String gridLeft;
    private final String gridRight;
    private final String gridTop;
    private final String gridBottom;

    public ChartTheme(String fontFamily, int defaultFontSize, double defaultLineWidth, String axisColor, String textColor,
                      String backgroundColor, List<String> palette, String gridLeft, String gridRight,
                      String gridTop, String gridBottom) {
        this.fontFamily = fontFamily;
        this.defaultFontSize = ValidationSupport.requirePositive(defaultFontSize, "defaultFontSize");
        this.defaultLineWidth = ValidationSupport.requireNonNegativeNullable(Double.valueOf(defaultLineWidth), "defaultLineWidth");
        this.axisColor = axisColor;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.palette = Collections.unmodifiableList(new ArrayList<String>(
                validatePalette(palette)
        ));
        this.gridLeft = gridLeft;
        this.gridRight = gridRight;
        this.gridTop = gridTop;
        this.gridBottom = gridBottom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static ChartTheme report() {
        return builder()
                .fontFamily("Noto Sans")
                .defaultFontSize(12)
                .defaultLineWidth(1.2d)
                .axisColor("#222222")
                .textColor("#222222")
                .backgroundColor("#ffffff")
                .palette(Arrays.asList("#006e54", "#bb1005", "#0095d9", "#674598", "#8c6450", "#7b6c3e"))
                .gridLeft("8%")
                .gridRight("6%")
                .gridTop("10%")
                .gridBottom("16%")
                .build();
    }

    public static ChartTheme minimal() {
        return builder()
                .fontFamily("Noto Sans")
                .defaultFontSize(11)
                .defaultLineWidth(1.0d)
                .axisColor("#333333")
                .textColor("#333333")
                .backgroundColor("#ffffff")
                .palette(Arrays.asList("#2f7ed8", "#0d233a", "#8bbc21", "#910000"))
                .gridLeft("7%")
                .gridRight("6%")
                .gridTop("10%")
                .gridBottom("14%")
                .build();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public int getDefaultFontSize() {
        return defaultFontSize;
    }

    public double getDefaultLineWidth() {
        return defaultLineWidth;
    }

    public String getAxisColor() {
        return axisColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public List<String> getPalette() {
        return palette;
    }

    public String getGridLeft() {
        return gridLeft;
    }

    public String getGridRight() {
        return gridRight;
    }

    public String getGridTop() {
        return gridTop;
    }

    public String getGridBottom() {
        return gridBottom;
    }

    public String paletteAt(int index) {
        if (palette.isEmpty()) {
            return "#2f7ed8";
        }
        return palette.get(index % palette.size());
    }

    /**
     * Builder for incrementally customizing a theme without copying a large constructor call.
     */
    public static final class Builder {
        private String fontFamily = "Noto Sans";
        private int defaultFontSize = 12;
        private double defaultLineWidth = 1.2d;
        private String axisColor = "#222222";
        private String textColor = "#222222";
        private String backgroundColor = "#ffffff";
        private List<String> palette = new ArrayList<String>(Arrays.asList(
                "#006e54", "#bb1005", "#0095d9", "#674598", "#8c6450", "#7b6c3e"
        ));
        private String gridLeft = "8%";
        private String gridRight = "6%";
        private String gridTop = "10%";
        private String gridBottom = "16%";

        private Builder() {
        }

        private Builder(ChartTheme base) {
            Objects.requireNonNull(base, "base");
            this.fontFamily = base.fontFamily;
            this.defaultFontSize = base.defaultFontSize;
            this.defaultLineWidth = base.defaultLineWidth;
            this.axisColor = base.axisColor;
            this.textColor = base.textColor;
            this.backgroundColor = base.backgroundColor;
            this.palette = new ArrayList<String>(base.palette);
            this.gridLeft = base.gridLeft;
            this.gridRight = base.gridRight;
            this.gridTop = base.gridTop;
            this.gridBottom = base.gridBottom;
        }

        public Builder fontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        public Builder defaultFontSize(int defaultFontSize) {
            this.defaultFontSize = ValidationSupport.requirePositive(defaultFontSize, "defaultFontSize");
            return this;
        }

        public Builder defaultLineWidth(double defaultLineWidth) {
            this.defaultLineWidth = ValidationSupport.requireNonNegativeNullable(Double.valueOf(defaultLineWidth), "defaultLineWidth");
            return this;
        }

        public Builder axisColor(String axisColor) {
            this.axisColor = axisColor;
            return this;
        }

        public Builder textColor(String textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder backgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder palette(List<String> palette) {
            this.palette = palette == null
                    ? new ArrayList<String>()
                    : new ArrayList<String>(palette);
            return this;
        }

        public Builder palette(String... palette) {
            return palette(palette == null ? null : Arrays.asList(palette));
        }

        public Builder gridLeft(String gridLeft) {
            this.gridLeft = gridLeft;
            return this;
        }

        public Builder gridRight(String gridRight) {
            this.gridRight = gridRight;
            return this;
        }

        public Builder gridTop(String gridTop) {
            this.gridTop = gridTop;
            return this;
        }

        public Builder gridBottom(String gridBottom) {
            this.gridBottom = gridBottom;
            return this;
        }

        public ChartTheme build() {
            return new ChartTheme(fontFamily, defaultFontSize, defaultLineWidth, axisColor, textColor,
                    backgroundColor, palette, gridLeft, gridRight, gridTop, gridBottom);
        }
    }

    private static List<String> validatePalette(List<String> palette) {
        List<String> source = palette == null ? Collections.<String>emptyList() : palette;
        ArrayList<String> copy = new ArrayList<String>(source.size());
        for (int i = 0; i < source.size(); i++) {
            String color = source.get(i);
            if (color == null) {
                throw new NullPointerException("palette[" + i + "]");
            }
            copy.add(color);
        }
        return copy;
    }
}
