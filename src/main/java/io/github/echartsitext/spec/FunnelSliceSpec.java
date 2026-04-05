package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable funnel step definition.
 */
public final class FunnelSliceSpec {
    private final String name;
    private final Number value;
    private final Map<String, Object> extensions;

    FunnelSliceSpec(String name, Number value, Map<String, Object> extensions) {
        this.name = ValidationSupport.requireNonBlank(name, "name");
        this.value = value;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder(String name, Number value) {
        return new Builder(name, value);
    }

    public String getName() {
        return name;
    }

    Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("name", name);
        item.put("value", value);
        item.putAll(extensions);
        return item;
    }

    /**
     * Fluent builder for funnel steps.
     */
    public static final class Builder {
        private final String name;
        private final Number value;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String name, Number value) {
            this.name = name;
            this.value = value;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public FunnelSliceSpec build() {
            return new FunnelSliceSpec(name, value, extensions);
        }
    }
}
