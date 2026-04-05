package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed funnel series.
 */
public final class FunnelSeriesSpec {
    private final String name;
    private final List<FunnelSliceSpec> data;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final Number min;
    private final Number max;
    private final String minSize;
    private final String maxSize;
    private final String sort;
    private final Integer gap;
    private final Boolean labelShow;
    private final Map<String, Object> extensions;

    FunnelSeriesSpec(String name, List<FunnelSliceSpec> data, String left, String right, String top, String bottom,
                     Number min, Number max, String minSize, String maxSize, String sort, Integer gap,
                     Boolean labelShow, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.min = min;
        this.max = max;
        if (this.min != null && this.max != null && this.min.doubleValue() > this.max.doubleValue()) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.sort = sort;
        this.gap = ValidationSupport.requireNonNegativeNullable(gap, "gap");
        this.labelShow = labelShow;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<FunnelSliceSpec> getData() {
        return data;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "funnel");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        putIfNotNull(series, "left", left);
        putIfNotNull(series, "right", right);
        putIfNotNull(series, "top", top);
        putIfNotNull(series, "bottom", bottom);
        putIfNotNull(series, "min", min);
        putIfNotNull(series, "max", max);
        putIfNotNull(series, "minSize", minSize);
        putIfNotNull(series, "maxSize", maxSize);
        putIfNotNull(series, "sort", sort);
        putIfNotNull(series, "gap", gap);
        if (labelShow != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", labelShow);
            series.put("label", label);
        }
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>(data.size());
        for (FunnelSliceSpec item : data) {
            items.add(item.toOptionMap());
        }
        series.put("data", items);
        series.putAll(extensions);
        return series;
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    /**
     * Fluent builder for funnel series.
     */
    public static final class Builder {
        private String name;
        private List<FunnelSliceSpec> data = Collections.emptyList();
        private String left = "10%";
        private String right;
        private String top = "15%";
        private String bottom = "10%";
        private Number min;
        private Number max;
        private String minSize = "0%";
        private String maxSize = "100%";
        private String sort = "descending";
        private Integer gap = Integer.valueOf(2);
        private Boolean labelShow = Boolean.TRUE;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<FunnelSliceSpec> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder position(String left, String top, String right, String bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            return this;
        }

        public Builder range(Number min, Number max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder sizeRange(String minSize, String maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder gap(Integer gap) {
            this.gap = ValidationSupport.requireNonNegativeNullable(gap, "gap");
            return this;
        }

        public Builder labelShow(Boolean labelShow) {
            this.labelShow = labelShow;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public FunnelSeriesSpec build() {
            return new FunnelSeriesSpec(name, data, left, right, top, bottom, min, max, minSize, maxSize,
                    sort, gap, labelShow, extensions);
        }
    }
}
