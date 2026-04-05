package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed radar series.
 */
public final class RadarSeriesSpec {
    private final String name;
    private final List<RadarValueSpec> data;
    private final String symbol;
    private final Integer symbolSize;
    private final Double areaOpacity;
    private final Map<String, Object> extensions;

    RadarSeriesSpec(String name, List<RadarValueSpec> data, String symbol, Integer symbolSize,
                    Double areaOpacity, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.symbol = symbol;
        this.symbolSize = ValidationSupport.requirePositiveNullable(symbolSize, "symbolSize");
        this.areaOpacity = ValidationSupport.requireNonNegativeNullable(areaOpacity, "areaOpacity");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<RadarValueSpec> getData() {
        return data;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "radar");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        if (symbol != null) {
            series.put("symbol", symbol);
        }
        if (symbolSize != null) {
            series.put("symbolSize", symbolSize);
        }
        if (areaOpacity != null) {
            LinkedHashMap<String, Object> areaStyle = new LinkedHashMap<String, Object>();
            areaStyle.put("opacity", areaOpacity);
            series.put("areaStyle", areaStyle);
        }
        ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>(data.size());
        for (RadarValueSpec item : data) {
            values.add(item.toOptionMap());
        }
        series.put("data", values);
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for radar series.
     */
    public static final class Builder {
        private String name;
        private List<RadarValueSpec> data = Collections.emptyList();
        private String symbol = "circle";
        private Integer symbolSize = Integer.valueOf(6);
        private Double areaOpacity;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<RadarValueSpec> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
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

        public Builder areaOpacity(Double areaOpacity) {
            this.areaOpacity = ValidationSupport.requireNonNegativeNullable(areaOpacity, "areaOpacity");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public RadarSeriesSpec build() {
            return new RadarSeriesSpec(name, data, symbol, symbolSize, areaOpacity, extensions);
        }
    }
}
