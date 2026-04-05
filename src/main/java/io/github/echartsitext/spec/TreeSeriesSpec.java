package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed tree series.
 */
public final class TreeSeriesSpec {
    private final String name;
    private final List<HierarchyNodeSpec> data;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final String orient;
    private final String symbol;
    private final Integer symbolSize;
    private final Boolean expandAndCollapse;
    private final Integer initialTreeDepth;
    private final String labelPosition;
    private final String leavesLabelPosition;
    private final Map<String, Object> extensions;

    TreeSeriesSpec(String name, List<HierarchyNodeSpec> data, String left, String right, String top, String bottom,
                   String orient, String symbol, Integer symbolSize, Boolean expandAndCollapse,
                   Integer initialTreeDepth, String labelPosition, String leavesLabelPosition,
                   Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.orient = orient;
        this.symbol = symbol;
        this.symbolSize = ValidationSupport.requirePositiveNullable(symbolSize, "symbolSize");
        this.expandAndCollapse = expandAndCollapse;
        this.initialTreeDepth = initialTreeDepth;
        this.labelPosition = labelPosition;
        this.leavesLabelPosition = leavesLabelPosition;
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
        series.put("type", "tree");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        putIfNotNull(series, "left", left);
        putIfNotNull(series, "right", right);
        putIfNotNull(series, "top", top);
        putIfNotNull(series, "bottom", bottom);
        putIfNotNull(series, "orient", orient);
        putIfNotNull(series, "symbol", symbol);
        putIfNotNull(series, "symbolSize", symbolSize);
        putIfNotNull(series, "expandAndCollapse", expandAndCollapse);
        putIfNotNull(series, "initialTreeDepth", initialTreeDepth);
        if (labelPosition != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", Boolean.TRUE);
            label.put("position", labelPosition);
            series.put("label", label);
        }
        if (leavesLabelPosition != null) {
            LinkedHashMap<String, Object> leaves = new LinkedHashMap<String, Object>();
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", Boolean.TRUE);
            label.put("position", leavesLabelPosition);
            leaves.put("label", label);
            series.put("leaves", leaves);
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
     * Fluent builder for tree series.
     */
    public static final class Builder {
        private String name;
        private List<HierarchyNodeSpec> data = Collections.emptyList();
        private String left = "8%";
        private String right = "20%";
        private String top = "12%";
        private String bottom = "12%";
        private String orient = "LR";
        private String symbol = "emptyCircle";
        private Integer symbolSize = Integer.valueOf(10);
        private Boolean expandAndCollapse = Boolean.TRUE;
        private Integer initialTreeDepth = Integer.valueOf(-1);
        private String labelPosition = "left";
        private String leavesLabelPosition = "right";
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

        public Builder orient(String orient) {
            this.orient = orient;
            if ("LR".equalsIgnoreCase(orient) || "RL".equalsIgnoreCase(orient)) {
                this.labelPosition = "left";
                this.leavesLabelPosition = "right";
            }
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder symbolSize(Integer symbolSize) {
            this.symbolSize = ValidationSupport.requirePositiveNullable(symbolSize, "symbolSize");
            return this;
        }

        public Builder expandAndCollapse(Boolean expandAndCollapse) {
            this.expandAndCollapse = expandAndCollapse;
            return this;
        }

        public Builder initialTreeDepth(Integer initialTreeDepth) {
            this.initialTreeDepth = initialTreeDepth;
            return this;
        }

        public Builder labelPosition(String labelPosition) {
            this.labelPosition = labelPosition;
            return this;
        }

        public Builder leavesLabelPosition(String leavesLabelPosition) {
            this.leavesLabelPosition = leavesLabelPosition;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public TreeSeriesSpec build() {
            return new TreeSeriesSpec(name, data, left, right, top, bottom, orient, symbol, symbolSize,
                    expandAndCollapse, initialTreeDepth, labelPosition, leavesLabelPosition, extensions);
        }
    }
}
