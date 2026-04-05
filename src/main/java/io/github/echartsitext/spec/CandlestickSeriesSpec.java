package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed candlestick series used by the first-class candlestick builder.
 */
public final class CandlestickSeriesSpec {
    private final String name;
    private final List<CandlestickValue> data;
    private final String upColor;
    private final String downColor;
    private final String upBorderColor;
    private final String downBorderColor;
    private final Map<String, Object> extensions;

    CandlestickSeriesSpec(String name, List<CandlestickValue> data, String upColor, String downColor,
                          String upBorderColor, String downBorderColor, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.upColor = upColor;
        this.downColor = downColor;
        this.upBorderColor = upBorderColor;
        this.downBorderColor = downBorderColor;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "candlestick");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        ArrayList<List<Number>> values = new ArrayList<List<Number>>(data.size());
        for (CandlestickValue item : data) {
            ArrayList<Number> row = new ArrayList<Number>(4);
            row.add(item.getOpen());
            row.add(item.getClose());
            row.add(item.getLow());
            row.add(item.getHigh());
            values.add(row);
        }
        series.put("data", values);
        LinkedHashMap<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        putIfNotNull(itemStyle, "color", upColor);
        putIfNotNull(itemStyle, "color0", downColor);
        putIfNotNull(itemStyle, "borderColor", upBorderColor);
        putIfNotNull(itemStyle, "borderColor0", downBorderColor);
        if (!itemStyle.isEmpty()) {
            series.put("itemStyle", itemStyle);
        }
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for typed candlestick-series configuration.
     */
    public static final class Builder {
        private String name;
        private List<CandlestickValue> data = Collections.emptyList();
        private String upColor = "#c23531";
        private String downColor = "#314656";
        private String upBorderColor = "#c23531";
        private String downBorderColor = "#314656";
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<CandlestickValue> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder upColor(String upColor) {
            this.upColor = upColor;
            return this;
        }

        public Builder downColor(String downColor) {
            this.downColor = downColor;
            return this;
        }

        public Builder upBorderColor(String upBorderColor) {
            this.upBorderColor = upBorderColor;
            return this;
        }

        public Builder downBorderColor(String downBorderColor) {
            this.downBorderColor = downBorderColor;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public CandlestickSeriesSpec build() {
            return new CandlestickSeriesSpec(name, data, upColor, downColor, upBorderColor, downBorderColor, extensions);
        }
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }
}
