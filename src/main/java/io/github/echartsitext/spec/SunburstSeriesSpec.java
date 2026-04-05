package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed sunburst series.
 */
public final class SunburstSeriesSpec {
    private final String name;
    private final List<HierarchyNodeSpec> data;
    private final String innerRadius;
    private final String outerRadius;
    private final String centerX;
    private final String centerY;
    private final String sort;
    private final Object labelRotate;
    private final Boolean labelShow;
    private final Map<String, Object> extensions;

    SunburstSeriesSpec(String name, List<HierarchyNodeSpec> data, String innerRadius, String outerRadius,
                       String centerX, String centerY, String sort, Object labelRotate,
                       Boolean labelShow, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.sort = sort;
        this.labelRotate = labelRotate;
        this.labelShow = labelShow;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<HierarchyNodeSpec> getData() {
        return data;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "sunburst");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>(data.size());
        for (HierarchyNodeSpec item : data) {
            items.add(item.toOptionMap());
        }
        series.put("data", items);
        if (innerRadius != null || outerRadius != null) {
            ArrayList<String> radius = new ArrayList<String>(2);
            radius.add(innerRadius == null ? "0%" : innerRadius);
            radius.add(outerRadius == null ? "72%" : outerRadius);
            series.put("radius", radius);
        }
        if (centerX != null || centerY != null) {
            ArrayList<String> center = new ArrayList<String>(2);
            center.add(centerX == null ? "50%" : centerX);
            center.add(centerY == null ? "52%" : centerY);
            series.put("center", center);
        }
        putIfNotNull(series, "sort", sort);
        if (labelShow != null || labelRotate != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            putIfNotNull(label, "show", labelShow);
            putIfNotNull(label, "rotate", labelRotate);
            if (!label.isEmpty()) {
                series.put("label", label);
            }
        }
        series.putAll(extensions);
        return series;
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    /**
     * Fluent builder for sunburst series.
     */
    public static final class Builder {
        private String name;
        private List<HierarchyNodeSpec> data = Collections.emptyList();
        private String innerRadius = "0%";
        private String outerRadius = "72%";
        private String centerX = "50%";
        private String centerY = "52%";
        private String sort = "desc";
        private Object labelRotate = "radial";
        private Boolean labelShow = Boolean.TRUE;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<HierarchyNodeSpec> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder radius(String innerRadius, String outerRadius) {
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;
            return this;
        }

        public Builder center(String centerX, String centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder labelRotate(Object labelRotate) {
            this.labelRotate = labelRotate;
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

        public SunburstSeriesSpec build() {
            return new SunburstSeriesSpec(name, data, innerRadius, outerRadius, centerX, centerY,
                    sort, labelRotate, labelShow, extensions);
        }
    }
}
