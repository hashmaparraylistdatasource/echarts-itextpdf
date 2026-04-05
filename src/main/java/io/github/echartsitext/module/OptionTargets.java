package io.github.echartsitext.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small helpers for module authors who need to work with nested ECharts structures.
 */
public final class OptionTargets {
    private OptionTargets() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> ensureMap(OptionTarget target, String key) {
        Object current = target.get(key);
        if (current == null) {
            Map<String, Object> value = new LinkedHashMap<String, Object>();
            target.put(key, value);
            return value;
        }
        if (!(current instanceof Map)) {
            throw new IllegalStateException("Option key '" + key + "' is not an object");
        }
        return (Map<String, Object>) current;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> ensureMap(Map<String, Object> source, String key) {
        Object current = source.get(key);
        if (current == null) {
            Map<String, Object> value = new LinkedHashMap<String, Object>();
            source.put(key, value);
            return value;
        }
        if (!(current instanceof Map)) {
            throw new IllegalStateException("Option key '" + key + "' is not an object");
        }
        return (Map<String, Object>) current;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> ensureList(Map<String, Object> source, String key) {
        Object current = source.get(key);
        if (current == null) {
            List<Object> value = new ArrayList<Object>();
            source.put(key, value);
            return value;
        }
        if (!(current instanceof List)) {
            throw new IllegalStateException("Option key '" + key + "' is not a list");
        }
        return (List<Object>) current;
    }
}
