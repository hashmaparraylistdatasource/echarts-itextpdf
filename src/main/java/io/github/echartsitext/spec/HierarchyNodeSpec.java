package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Immutable hierarchical node used by tree-shaped chart families such as treemap and sunburst.
 */
public final class HierarchyNodeSpec {
    private final String name;
    private final Number value;
    private final List<HierarchyNodeSpec> children;
    private final Map<String, Object> itemStyle;
    private final Map<String, Object> label;
    private final Map<String, Object> extensions;

    HierarchyNodeSpec(String name, Number value, List<HierarchyNodeSpec> children,
                      Map<String, Object> itemStyle, Map<String, Object> label, Map<String, Object> extensions) {
        this.name = ValidationSupport.requireNonBlank(name, "name");
        this.value = value;
        this.children = ValidationSupport.immutableListCopy(children, "children");
        this.itemStyle = itemStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(itemStyle, "itemStyle");
        this.label = label == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(label, "label");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
        if (this.value == null && this.children.isEmpty()) {
            throw new IllegalArgumentException("hierarchy node must define a value or at least one child");
        }
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public String getName() {
        return name;
    }

    public List<HierarchyNodeSpec> getChildren() {
        return children;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> node = new LinkedHashMap<String, Object>();
        node.put("name", name);
        if (value != null) {
            node.put("value", value);
        }
        if (!children.isEmpty()) {
            ArrayList<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>(children.size());
            for (HierarchyNodeSpec child : children) {
                childNodes.add(child.toOptionMap());
            }
            node.put("children", childNodes);
        }
        if (!itemStyle.isEmpty()) {
            node.put("itemStyle", new LinkedHashMap<String, Object>(itemStyle));
        }
        if (!label.isEmpty()) {
            node.put("label", new LinkedHashMap<String, Object>(label));
        }
        node.putAll(extensions);
        return node;
    }

    /**
     * Fluent builder for a hierarchical chart node.
     */
    public static final class Builder {
        private final String name;
        private Number value;
        private final List<HierarchyNodeSpec> children = new ArrayList<HierarchyNodeSpec>();
        private final Map<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> label = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String name) {
            this.name = ValidationSupport.requireNonBlank(name, "name");
        }

        public Builder value(Number value) {
            this.value = value;
            return this;
        }

        public Builder color(String color) {
            this.itemStyle.put("color", color);
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

        public Builder child(HierarchyNodeSpec child) {
            this.children.add(Objects.requireNonNull(child, "child"));
            return this;
        }

        public Builder child(String name, Number value) {
            return child(name, value, customizer -> {
            });
        }

        public Builder child(String name, Number value, Consumer<Builder> customizer) {
            Builder child = builder(name).value(value);
            Objects.requireNonNull(customizer, "customizer").accept(child);
            this.children.add(child.build());
            return this;
        }

        public Builder branch(String name, Consumer<Builder> customizer) {
            Builder child = builder(name);
            Objects.requireNonNull(customizer, "customizer").accept(child);
            this.children.add(child.build());
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public HierarchyNodeSpec build() {
            return new HierarchyNodeSpec(name, value, children, itemStyle, label, extensions);
        }
    }
}
