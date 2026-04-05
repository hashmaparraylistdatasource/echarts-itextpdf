package io.github.echartsitext.module;

import java.util.List;
import java.util.Map;

/**
 * Stable mutable target exposed to modules so they can augment the option tree
 * without depending on internal composition classes.
 */
public interface OptionTarget {
    void put(String key, Object value);

    void putIfNotNull(String key, Object value);

    void putAll(Map<String, Object> values);

    Object get(String key);

    void appendToList(String key, Object value);

    List<Object> ensureList(String key);

    List<Map<String, Object>> ensureObjectList(String key);
}
