package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes the legend area and optional raw legend extensions.
 */
public final class LegendSpec {
    private final boolean show;
    private final String orient;
    private final String left;
    private final String top;
    private final TextStyleSpec textStyle;
    private final Map<String, Object> extensions;

    LegendSpec(boolean show, String orient, String left, String top, TextStyleSpec textStyle, Map<String, Object> extensions) {
        this.show = show;
        this.orient = orient;
        this.left = left;
        this.top = top;
        this.textStyle = textStyle;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isShow() {
        return show;
    }

    public String getOrient() {
        return orient;
    }

    public String getLeft() {
        return left;
    }

    public String getTop() {
        return top;
    }

    public TextStyleSpec getTextStyle() {
        return textStyle;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Fluent builder for legend configuration.
     */
    public static final class Builder {
        private boolean show = true;
        private String orient = "horizontal";
        private String left = "center";
        private String top = "bottom";
        private TextStyleSpec textStyle;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder show(boolean show) {
            this.show = show;
            return this;
        }

        public Builder orient(String orient) {
            this.orient = orient;
            return this;
        }

        public Builder left(String left) {
            this.left = left;
            return this;
        }

        public Builder top(String top) {
            this.top = top;
            return this;
        }

        public Builder textStyle(TextStyleSpec textStyle) {
            this.textStyle = textStyle;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public LegendSpec build() {
            return new LegendSpec(show, orient, left, top, textStyle, extensions);
        }
    }
}
