package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable 3D axis definition used by typed 3D chart builders.
 */
public final class Axis3DSpec {
    private final String type;
    private final String name;
    private final List<Object> data;
    private final Double min;
    private final Double max;
    private final Double interval;
    private final Integer splitNumber;
    private final Map<String, Object> extensions;

    Axis3DSpec(String type, String name, List<Object> data, Double min, Double max, Double interval,
               Integer splitNumber, Map<String, Object> extensions) {
        this.type = ValidationSupport.requireNonBlank(type, "type");
        this.name = name;
        this.data = Collections.unmodifiableList(copyValues(data, "data"));
        this.min = min;
        this.max = max;
        this.interval = ValidationSupport.requirePositiveNullable(interval, "interval");
        this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder category() {
        return builder().type("category");
    }

    public static Builder value() {
        return builder().type("value");
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> axis = new LinkedHashMap<String, Object>();
        axis.put("type", type);
        if (name != null) {
            axis.put("name", name);
        }
        if (!data.isEmpty()) {
            axis.put("data", data);
        }
        if (min != null) {
            axis.put("min", min);
        }
        if (max != null) {
            axis.put("max", max);
        }
        if (interval != null) {
            axis.put("interval", interval);
        }
        if (splitNumber != null) {
            axis.put("splitNumber", splitNumber);
        }
        axis.putAll(extensions);
        return axis;
    }

    /**
     * Fluent builder for 3D axis configuration.
     */
    public static final class Builder {
        private String type = "value";
        private String name = "";
        private List<Object> data = Collections.emptyList();
        private Double min;
        private Double max;
        private Double interval;
        private Integer splitNumber;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder type(String type) {
            this.type = ValidationSupport.requireNonBlank(type, "type");
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder categories(List<?> data) {
            this.data = copyValues(data, "data");
            return this;
        }

        public Builder range(Double min, Double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder interval(Double interval) {
            this.interval = ValidationSupport.requirePositiveNullable(interval, "interval");
            return this;
        }

        public Builder splitNumber(Integer splitNumber) {
            this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Axis3DSpec build() {
            return new Axis3DSpec(type, name, data, min, max, interval, splitNumber, extensions);
        }
    }

    private static List<Object> copyValues(List<?> values, String name) {
        if (values == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> copy = new ArrayList<Object>(values.size());
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value == null) {
                throw new NullPointerException(name + "[" + i + "]");
            }
            copy.add(value);
        }
        return copy;
    }
}
