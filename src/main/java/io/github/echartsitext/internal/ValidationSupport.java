package io.github.echartsitext.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Shared argument validation helpers for internal library use.
 */
public final class ValidationSupport {
    private ValidationSupport() {
    }

    public static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    public static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }

    public static long requirePositive(long value, String name) {
        if (value <= 0L) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }

    public static Integer requirePositiveNullable(Integer value, String name) {
        if (value == null) {
            return null;
        }
        requirePositive(value.intValue(), name);
        return value;
    }

    public static Double requirePositiveNullable(Double value, String name) {
        if (value == null) {
            return null;
        }
        if (value.doubleValue() <= 0d) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }

    public static Double requireNonNegativeNullable(Double value, String name) {
        if (value == null) {
            return null;
        }
        if (value.doubleValue() < 0d) {
            throw new IllegalArgumentException(name + " must be greater than or equal to 0");
        }
        return value;
    }

    public static Integer requireNonNegativeNullable(Integer value, String name) {
        if (value == null) {
            return null;
        }
        if (value.intValue() < 0) {
            throw new IllegalArgumentException(name + " must be greater than or equal to 0");
        }
        return value;
    }

    public static Number requireNonNegativeNullable(Number value, String name) {
        if (value == null) {
            return null;
        }
        if (value.doubleValue() < 0d) {
            throw new IllegalArgumentException(name + " must be greater than or equal to 0");
        }
        return value;
    }

    public static <T> List<T> mutableListCopy(List<T> values, String name) {
        Objects.requireNonNull(values, name);
        ArrayList<T> copy = new ArrayList<T>(values.size());
        int index = 0;
        for (T value : values) {
            if (value == null) {
                throw new NullPointerException(name + "[" + index + "]");
            }
            copy.add(value);
            index++;
        }
        return copy;
    }

    public static <T> List<T> immutableListCopy(List<T> values, String name) {
        return Collections.unmodifiableList(mutableListCopy(values, name));
    }

    public static List<Object> mutableObjectListCopy(List<?> values, String name) {
        Objects.requireNonNull(values, name);
        ArrayList<Object> copy = new ArrayList<Object>(values.size());
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value == null) {
                throw new NullPointerException(name + "[" + i + "]");
            }
            copy.add(deepCopyValue(value));
        }
        return copy;
    }

    public static List<Object> immutableObjectListCopy(List<?> values, String name) {
        return Collections.unmodifiableList(mutableObjectListCopy(values, name));
    }

    public static Map<String, Object> mutableMapCopy(Map<String, Object> values, String name) {
        Objects.requireNonNull(values, name);
        LinkedHashMap<String, Object> copy = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException(name + " contains a null key");
            }
            copy.put(entry.getKey(), deepCopyValue(entry.getValue()));
        }
        return copy;
    }

    public static Map<String, Object> immutableMapCopy(Map<String, Object> values, String name) {
        return Collections.unmodifiableMap(mutableMapCopy(values, name));
    }

    @SuppressWarnings("unchecked")
    public static Object deepCopyValue(Object value) {
        if (value instanceof Map) {
            return mutableMapCopy((Map<String, Object>) value, "value");
        }
        if (value instanceof List) {
            List<Object> copy = new ArrayList<Object>();
            for (Object item : (List<Object>) value) {
                copy.add(deepCopyValue(item));
            }
            return copy;
        }
        return value;
    }
}
