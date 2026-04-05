package io.github.echartsitext.option;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small fluent helper for building ordered ECharts option objects without repetitive map boilerplate.
 */
final class OptionMapBuilder {
    private final LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();

    private OptionMapBuilder() {
    }

    static OptionMapBuilder create() {
        return new OptionMapBuilder();
    }

    OptionMapBuilder put(String key, Object value) {
        values.put(key, value);
        return this;
    }

    OptionMapBuilder putIfNotNull(String key, Object value) {
        OptionSupport.putIfNotNull(values, key, value);
        return this;
    }

    OptionMapBuilder putAll(Map<String, Object> additionalValues) {
        if (additionalValues != null && !additionalValues.isEmpty()) {
            values.putAll(additionalValues);
        }
        return this;
    }

    LinkedHashMap<String, Object> build() {
        return values;
    }
}
