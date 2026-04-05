package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

/**
 * Shared text style fragment reused by titles, legends, and axis labels.
 */
public final class TextStyleSpec {
    private final String fontFamily;
    private final Integer fontSize;
    private final String fontWeight;
    private final String color;

    TextStyleSpec(String fontFamily, Integer fontSize, String fontWeight, String color) {
        this.fontFamily = fontFamily;
        this.fontSize = ValidationSupport.requirePositiveNullable(fontSize, "fontSize");
        this.fontWeight = fontWeight;
        this.color = color;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getColor() {
        return color;
    }

    /**
     * Fluent builder for optional text style attributes.
     */
    public static final class Builder {
        private String fontFamily;
        private Integer fontSize;
        private String fontWeight;
        private String color;

        public Builder fontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        public Builder fontSize(Integer fontSize) {
            this.fontSize = ValidationSupport.requirePositiveNullable(fontSize, "fontSize");
            return this;
        }

        public Builder fontWeight(String fontWeight) {
            this.fontWeight = fontWeight;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public TextStyleSpec build() {
            return new TextStyleSpec(fontFamily, fontSize, fontWeight, color);
        }
    }
}
