package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed boxplot series used by the first-class boxplot builder.
 */
public final class BoxplotSeriesSpec {
    private final String name;
    private final List<BoxplotValue> data;
    private final String color;
    private final String borderColor;
    private final String minBoxWidth;
    private final String maxBoxWidth;
    private final Map<String, Object> itemStyle;
    private final Map<String, Object> extensions;

    BoxplotSeriesSpec(String name, List<BoxplotValue> data, String color, String borderColor,
                      String minBoxWidth, String maxBoxWidth, Map<String, Object> itemStyle, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.color = color;
        this.borderColor = borderColor;
        this.minBoxWidth = minBoxWidth;
        this.maxBoxWidth = maxBoxWidth;
        this.itemStyle = itemStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(itemStyle, "itemStyle");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "boxplot");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        ArrayList<List<Number>> values = new ArrayList<List<Number>>(data.size());
        for (BoxplotValue item : data) {
            ArrayList<Number> row = new ArrayList<Number>(5);
            row.add(item.getMin());
            row.add(item.getQ1());
            row.add(item.getMedian());
            row.add(item.getQ3());
            row.add(item.getMax());
            values.add(row);
        }
        series.put("data", values);
        if (minBoxWidth != null || maxBoxWidth != null) {
            ArrayList<String> boxWidth = new ArrayList<String>(2);
            boxWidth.add(minBoxWidth == null ? "35%" : minBoxWidth);
            boxWidth.add(maxBoxWidth == null ? "70%" : maxBoxWidth);
            series.put("boxWidth", boxWidth);
        }
        LinkedHashMap<String, Object> mergedItemStyle = new LinkedHashMap<String, Object>(itemStyle);
        putIfNotNull(mergedItemStyle, "color", color);
        putIfNotNull(mergedItemStyle, "borderColor", borderColor);
        if (!mergedItemStyle.isEmpty()) {
            series.put("itemStyle", mergedItemStyle);
        }
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for boxplot-series configuration.
     */
    public static final class Builder {
        private String name;
        private List<BoxplotValue> data = Collections.emptyList();
        private String color = "#d8ecff";
        private String borderColor = "#2f4554";
        private String minBoxWidth = "35%";
        private String maxBoxWidth = "70%";
        private final Map<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<BoxplotValue> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder borderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder boxWidth(String minBoxWidth, String maxBoxWidth) {
            this.minBoxWidth = minBoxWidth;
            this.maxBoxWidth = maxBoxWidth;
            return this;
        }

        public Builder itemStyle(String key, Object value) {
            this.itemStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public BoxplotSeriesSpec build() {
            return new BoxplotSeriesSpec(name, data, color, borderColor, minBoxWidth, maxBoxWidth, itemStyle, extensions);
        }
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }
}
