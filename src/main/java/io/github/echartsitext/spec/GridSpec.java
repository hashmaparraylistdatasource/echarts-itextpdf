package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes the plot area margins and background settings.
 */
public final class GridSpec {
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final Boolean containLabel;
    private final Boolean showBackground;
    private final String backgroundColor;
    private final Map<String, Object> extensions;

    GridSpec(String left, String right, String top, String bottom, Boolean containLabel, Boolean showBackground, String backgroundColor, Map<String, Object> extensions) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.containLabel = containLabel;
        this.showBackground = showBackground;
        this.backgroundColor = backgroundColor;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String getTop() {
        return top;
    }

    public String getBottom() {
        return bottom;
    }

    public Boolean getContainLabel() {
        return containLabel;
    }

    public Boolean getShowBackground() {
        return showBackground;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Fluent builder for grid configuration.
     */
    public static final class Builder {
        private String left;
        private String right;
        private String top;
        private String bottom;
        private Boolean containLabel = Boolean.TRUE;
        private Boolean showBackground = Boolean.FALSE;
        private String backgroundColor;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder left(String left) {
            this.left = left;
            return this;
        }

        public Builder right(String right) {
            this.right = right;
            return this;
        }

        public Builder top(String top) {
            this.top = top;
            return this;
        }

        public Builder bottom(String bottom) {
            this.bottom = bottom;
            return this;
        }

        public Builder containLabel(Boolean containLabel) {
            this.containLabel = containLabel;
            return this;
        }

        public Builder showBackground(Boolean showBackground) {
            this.showBackground = showBackground;
            return this;
        }

        public Builder backgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public GridSpec build() {
            return new GridSpec(left, right, top, bottom, containLabel, showBackground, backgroundColor, extensions);
        }
    }
}
