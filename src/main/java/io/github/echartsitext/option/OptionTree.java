package io.github.echartsitext.option;

import io.github.echartsitext.module.OptionTarget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutable intermediate tree used while composing a final ECharts option.
 */
final class OptionTree implements OptionTarget {
    private final LinkedHashMap<String, Object> root = new LinkedHashMap<String, Object>();

    @Override
    public void put(String key, Object value) {
        root.put(key, value);
    }

    @Override
    public void putIfNotNull(String key, Object value) {
        if (value != null) {
            root.put(key, value);
        }
    }

    @Override
    public void putAll(Map<String, Object> values) {
        if (values != null && !values.isEmpty()) {
            root.putAll(values);
        }
    }

    @Override
    public Object get(String key) {
        return root.get(key);
    }

    @Override
    public void appendToList(String key, Object value) {
        ensureList(key).add(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> ensureList(String key) {
        Object current = root.get(key);
        if (current == null) {
            List<Object> values = new ArrayList<Object>();
            root.put(key, values);
            return values;
        }
        if (!(current instanceof List)) {
            throw new IllegalStateException("Option key '" + key + "' is not a list");
        }
        return (List<Object>) current;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ensureObjectList(String key) {
        Object current = root.get(key);
        if (current == null) {
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            root.put(key, values);
            return values;
        }
        if (!(current instanceof List)) {
            throw new IllegalStateException("Option key '" + key + "' is not a list");
        }
        return (List<Map<String, Object>>) current;
    }

    public Map<String, Object> asMap() {
        return new LinkedHashMap<String, Object>(root);
    }
}
