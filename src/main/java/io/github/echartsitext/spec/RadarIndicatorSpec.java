package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable radar indicator definition.
 */
public final class RadarIndicatorSpec {
    private final String name;
    private final Number min;
    private final Number max;
    private final String color;
    private final Map<String, Object> extensions;

    RadarIndicatorSpec(String name, Number min, Number max, String color, Map<String, Object> extensions) {
        this.name = ValidationSupport.requireNonBlank(name, "name");
        this.min = min;
        this.max = max;
        if (this.min != null && this.max != null && this.min.doubleValue() > this.max.doubleValue()) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.color = color;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder(String name) {
        return new Builder().name(name);
    }

    public String getName() {
        return name;
    }

    Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> indicator = new LinkedHashMap<String, Object>();
        indicator.put("name", name);
        if (min != null) {
            indicator.put("min", min);
        }
        if (max != null) {
            indicator.put("max", max);
        }
        if (color != null) {
            indicator.put("color", color);
        }
        indicator.putAll(extensions);
        return indicator;
    }

    /**
     * Fluent builder for radar indicators.
     */
    public static final class Builder {
        private String name;
        private Number min;
        private Number max;
        private String color;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder min(Number min) {
            this.min = min;
            return this;
        }

        public Builder max(Number max) {
            this.max = max;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public RadarIndicatorSpec build() {
            return new RadarIndicatorSpec(name, min, max, color, extensions);
        }
    }
}
