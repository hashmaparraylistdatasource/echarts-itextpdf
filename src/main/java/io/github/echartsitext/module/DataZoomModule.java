package io.github.echartsitext.module;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adds one top-level dataZoom block, which is the most common way to enable interactive zoom.
 */
public final class DataZoomModule implements OptionModule {
    private final String type;
    private final Integer xAxisIndex;
    private final Integer yAxisIndex;
    private final Integer start;
    private final Integer end;
    private final String filterMode;
    private final Map<String, Object> extensions;

    private DataZoomModule(String type, Integer xAxisIndex, Integer yAxisIndex, Integer start, Integer end,
                           String filterMode, Map<String, Object> extensions) {
        this.type = ValidationSupport.requireNonBlank(type, "type");
        this.xAxisIndex = ValidationSupport.requireNonNegativeNullable(xAxisIndex, "xAxisIndex");
        this.yAxisIndex = ValidationSupport.requireNonNegativeNullable(yAxisIndex, "yAxisIndex");
        this.start = requirePercentage(start, "start");
        this.end = requirePercentage(end, "end");
        if (this.start != null && this.end != null && this.start.intValue() > this.end.intValue()) {
            throw new IllegalArgumentException("start must be less than or equal to end");
        }
        this.filterMode = filterMode;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder inside() {
        return new Builder("inside");
    }

    public static Builder slider() {
        return new Builder("slider");
    }

    @Override
    public void apply(OptionTarget target, ModuleContext context) {
        LinkedHashMap<String, Object> zoom = new LinkedHashMap<String, Object>();
        zoom.put("type", type);
        putIfNotNull(zoom, "xAxisIndex", xAxisIndex);
        putIfNotNull(zoom, "yAxisIndex", yAxisIndex);
        putIfNotNull(zoom, "start", start);
        putIfNotNull(zoom, "end", end);
        putIfNotNull(zoom, "filterMode", filterMode);
        zoom.putAll(extensions);
        target.appendToList("dataZoom", zoom);
    }

    private void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    /**
     * Builder for dataZoom modules so callers can stay strongly typed for common cases.
     */
    public static final class Builder {
        private final String type;
        private Integer xAxisIndex;
        private Integer yAxisIndex;
        private Integer start;
        private Integer end;
        private String filterMode;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String type) {
            this.type = ValidationSupport.requireNonBlank(type, "type");
        }

        public Builder xAxisIndex(Integer xAxisIndex) {
            this.xAxisIndex = ValidationSupport.requireNonNegativeNullable(xAxisIndex, "xAxisIndex");
            return this;
        }

        public Builder yAxisIndex(Integer yAxisIndex) {
            this.yAxisIndex = ValidationSupport.requireNonNegativeNullable(yAxisIndex, "yAxisIndex");
            return this;
        }

        public Builder start(Integer start) {
            this.start = requirePercentage(start, "start");
            return this;
        }

        public Builder end(Integer end) {
            this.end = requirePercentage(end, "end");
            return this;
        }

        public Builder filterMode(String filterMode) {
            this.filterMode = filterMode;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public DataZoomModule build() {
            return new DataZoomModule(type, xAxisIndex, yAxisIndex, start, end, filterMode, extensions);
        }
    }

    private static Integer requirePercentage(Integer value, String name) {
        if (value == null) {
            return null;
        }
        if (value.intValue() < 0 || value.intValue() > 100) {
            throw new IllegalArgumentException(name + " must be between 0 and 100");
        }
        return value;
    }
}
