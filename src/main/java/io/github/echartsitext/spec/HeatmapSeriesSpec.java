package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed heatmap series used by the first-class heatmap builder.
 */
public final class HeatmapSeriesSpec {
    private final String name;
    private final List<HeatmapPoint> data;
    private final Boolean labelShow;
    private final Map<String, Object> itemStyle;
    private final Map<String, Object> emphasis;
    private final Map<String, Object> extensions;

    HeatmapSeriesSpec(String name, List<HeatmapPoint> data, Boolean labelShow,
                      Map<String, Object> itemStyle, Map<String, Object> emphasis, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.labelShow = labelShow;
        this.itemStyle = itemStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(itemStyle, "itemStyle");
        this.emphasis = emphasis == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(emphasis, "emphasis");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "heatmap");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        ArrayList<List<Number>> values = new ArrayList<List<Number>>(data.size());
        for (HeatmapPoint point : data) {
            ArrayList<Number> row = new ArrayList<Number>(3);
            row.add(point.getX());
            row.add(point.getY());
            row.add(point.getValue());
            values.add(row);
        }
        series.put("data", values);
        if (labelShow != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", labelShow);
            series.put("label", label);
        }
        if (!itemStyle.isEmpty()) {
            series.put("itemStyle", new LinkedHashMap<String, Object>(itemStyle));
        }
        if (!emphasis.isEmpty()) {
            series.put("emphasis", new LinkedHashMap<String, Object>(emphasis));
        }
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for heatmap-series configuration.
     */
    public static final class Builder {
        private String name;
        private List<HeatmapPoint> data = Collections.emptyList();
        private Boolean labelShow = Boolean.FALSE;
        private final Map<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> emphasis = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<HeatmapPoint> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder labelShow(Boolean labelShow) {
            this.labelShow = labelShow;
            return this;
        }

        public Builder itemStyle(String key, Object value) {
            this.itemStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder emphasis(String key, Object value) {
            this.emphasis.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public HeatmapSeriesSpec build() {
            return new HeatmapSeriesSpec(name, data, labelShow, itemStyle, emphasis, extensions);
        }
    }
}
