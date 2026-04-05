package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed treemap series.
 */
public final class TreemapSeriesSpec {
    private final String name;
    private final List<HierarchyNodeSpec> data;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final Boolean roam;
    private final Boolean breadcrumbShow;
    private final Boolean labelShow;
    private final Integer leafDepth;
    private final Map<String, Object> extensions;

    TreemapSeriesSpec(String name, List<HierarchyNodeSpec> data, String left, String right, String top, String bottom,
                      Boolean roam, Boolean breadcrumbShow, Boolean labelShow, Integer leafDepth,
                      Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.roam = roam;
        this.breadcrumbShow = breadcrumbShow;
        this.labelShow = labelShow;
        this.leafDepth = ValidationSupport.requireNonNegativeNullable(leafDepth, "leafDepth");
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
        series.put("type", "treemap");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        putIfNotNull(series, "left", left);
        putIfNotNull(series, "right", right);
        putIfNotNull(series, "top", top);
        putIfNotNull(series, "bottom", bottom);
        putIfNotNull(series, "roam", roam);
        putIfNotNull(series, "leafDepth", leafDepth);
        if (breadcrumbShow != null) {
            LinkedHashMap<String, Object> breadcrumb = new LinkedHashMap<String, Object>();
            breadcrumb.put("show", breadcrumbShow);
            series.put("breadcrumb", breadcrumb);
        }
        if (labelShow != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", labelShow);
            series.put("label", label);
        }
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>(data.size());
        for (HierarchyNodeSpec item : data) {
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
     * Fluent builder for treemap series.
     */
    public static final class Builder {
        private String name;
        private List<HierarchyNodeSpec> data = Collections.emptyList();
        private String left = "4%";
        private String right = "4%";
        private String top = "10%";
        private String bottom = "10%";
        private Boolean roam = Boolean.FALSE;
        private Boolean breadcrumbShow = Boolean.TRUE;
        private Boolean labelShow = Boolean.TRUE;
        private Integer leafDepth;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<HierarchyNodeSpec> data) {
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

        public Builder roam(Boolean roam) {
            this.roam = roam;
            return this;
        }

        public Builder breadcrumbShow(Boolean breadcrumbShow) {
            this.breadcrumbShow = breadcrumbShow;
            return this;
        }

        public Builder labelShow(Boolean labelShow) {
            this.labelShow = labelShow;
            return this;
        }

        public Builder leafDepth(Integer leafDepth) {
            this.leafDepth = ValidationSupport.requireNonNegativeNullable(leafDepth, "leafDepth");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public TreemapSeriesSpec build() {
            return new TreemapSeriesSpec(name, data, left, right, top, bottom, roam, breadcrumbShow,
                    labelShow, leafDepth, extensions);
        }
    }
}
