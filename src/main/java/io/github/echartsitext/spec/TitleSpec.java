package io.github.echartsitext.spec;

/**
 * Describes the chart title block.
 */
public final class TitleSpec {
    private final String text;
    private final boolean show;
    private final String left;
    private final TextStyleSpec textStyle;

    TitleSpec(String text, boolean show, String left, TextStyleSpec textStyle) {
        this.text = text;
        this.show = show;
        this.left = left;
        this.textStyle = textStyle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getText() {
        return text;
    }

    public boolean isShow() {
        return show;
    }

    public String getLeft() {
        return left;
    }

    public TextStyleSpec getTextStyle() {
        return textStyle;
    }

    /**
     * Fluent builder for title metadata.
     */
    public static final class Builder {
        private String text = "";
        private boolean show;
        private String left = "center";
        private TextStyleSpec textStyle;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder show(boolean show) {
            this.show = show;
            return this;
        }

        public Builder left(String left) {
            this.left = left;
            return this;
        }

        public Builder textStyle(TextStyleSpec textStyle) {
            this.textStyle = textStyle;
            return this;
        }

        public TitleSpec build() {
            return new TitleSpec(text, show, left, textStyle);
        }
    }
}
