package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable pie-slice definition used by typed pie chart builders.
 */
public final class PieSliceSpec {
    private final String name;
    private final Number value;
    private final Boolean selected;
    private final Map<String, Object> itemStyle;
    private final Map<String, Object> label;
    private final Map<String, Object> extensions;

    PieSliceSpec(String name, Number value, Boolean selected,
                 Map<String, Object> itemStyle, Map<String, Object> label, Map<String, Object> extensions) {
        this.name = ValidationSupport.requireNonBlank(name, "name");
        this.value = value;
        this.selected = selected;
        this.itemStyle = itemStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(itemStyle, "itemStyle");
        this.label = label == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(label, "label");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder(String name, Number value) {
        return new Builder(name, value);
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> slice = new LinkedHashMap<String, Object>();
        slice.put("name", name);
        slice.put("value", value);
        if (selected != null) {
            slice.put("selected", selected);
        }
        if (!itemStyle.isEmpty()) {
            slice.put("itemStyle", new LinkedHashMap<String, Object>(itemStyle));
        }
        if (!label.isEmpty()) {
            slice.put("label", new LinkedHashMap<String, Object>(label));
        }
        slice.putAll(extensions);
        return slice;
    }

    /**
     * Fluent builder for a typed pie slice.
     */
    public static final class Builder {
        private final String name;
        private final Number value;
        private Boolean selected;
        private final Map<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> label = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String name, Number value) {
            this.name = ValidationSupport.requireNonBlank(name, "name");
            this.value = value;
        }

        public Builder selected(Boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder itemStyle(String key, Object value) {
            this.itemStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder label(String key, Object value) {
            this.label.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public PieSliceSpec build() {
            return new PieSliceSpec(name, value, selected, itemStyle, label, extensions);
        }
    }
}
