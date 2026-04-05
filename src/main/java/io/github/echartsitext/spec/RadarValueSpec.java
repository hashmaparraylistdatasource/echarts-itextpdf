package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable radar data row.
 */
public final class RadarValueSpec {
    private final String name;
    private final List<Number> values;
    private final Map<String, Object> extensions;

    RadarValueSpec(String name, List<Number> values, Map<String, Object> extensions) {
        this.name = ValidationSupport.requireNonBlank(name, "name");
        this.values = Collections.unmodifiableList(copyNumbers(values, "values"));
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder(String name, List<? extends Number> values) {
        return new Builder().name(name).values(values);
    }

    public String getName() {
        return name;
    }

    Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("name", name);
        item.put("value", values);
        item.putAll(extensions);
        return item;
    }

    private static List<Number> copyNumbers(List<? extends Number> values, String name) {
        List<? extends Number> safeValues = ValidationSupport.mutableListCopy(values, name);
        ArrayList<Number> copy = new ArrayList<Number>(safeValues.size());
        for (Number value : safeValues) {
            copy.add(value);
        }
        return copy;
    }

    /**
     * Fluent builder for radar values.
     */
    public static final class Builder {
        private String name;
        private List<Number> values = Collections.emptyList();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder values(List<? extends Number> values) {
            this.values = copyNumbers(values, "values");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public RadarValueSpec build() {
            return new RadarValueSpec(name, values, extensions);
        }
    }
}
