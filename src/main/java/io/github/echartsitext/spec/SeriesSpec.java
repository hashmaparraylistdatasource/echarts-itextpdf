package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a single ECharts series and its optional style fragments.
 */
public final class SeriesSpec {
    private final String type;
    private final String name;
    private final List<ChartPoint> data;
    private final String color;
    private final Boolean smooth;
    private final Double lineWidth;
    private final String symbol;
    private final Number symbolSize;
    private final Integer xAxisIndex;
    private final Integer yAxisIndex;
    private final String stack;
    private final Map<String, Object> markPoint;
    private final Map<String, Object> itemStyle;
    private final Map<String, Object> lineStyle;
    private final Map<String, Object> areaStyle;
    private final Map<String, Object> extensions;

    SeriesSpec(String type, String name, List<ChartPoint> data, String color, Boolean smooth, Double lineWidth,
               String symbol, Number symbolSize, Integer xAxisIndex, Integer yAxisIndex, String stack,
               Map<String, Object> markPoint, Map<String, Object> itemStyle, Map<String, Object> lineStyle,
               Map<String, Object> areaStyle, Map<String, Object> extensions) {
        this.type = ValidationSupport.requireNonBlank(type, "type");
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.color = color;
        this.smooth = smooth;
        this.lineWidth = ValidationSupport.requireNonNegativeNullable(lineWidth, "lineWidth");
        this.symbol = symbol;
        this.symbolSize = ValidationSupport.requireNonNegativeNullable(symbolSize, "symbolSize");
        this.xAxisIndex = ValidationSupport.requireNonNegativeNullable(xAxisIndex, "xAxisIndex");
        this.yAxisIndex = ValidationSupport.requireNonNegativeNullable(yAxisIndex, "yAxisIndex");
        this.stack = stack;
        this.markPoint = markPoint == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(markPoint, "markPoint");
        this.itemStyle = itemStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(itemStyle, "itemStyle");
        this.lineStyle = lineStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(lineStyle, "lineStyle");
        this.areaStyle = areaStyle == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(areaStyle, "areaStyle");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder(String type, String name, List<ChartPoint> data) {
        return new Builder(type, name, data);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<ChartPoint> getData() {
        return data;
    }

    public String getColor() {
        return color;
    }

    public Boolean getSmooth() {
        return smooth;
    }

    public Double getLineWidth() {
        return lineWidth;
    }

    public String getSymbol() {
        return symbol;
    }

    public Number getSymbolSize() {
        return symbolSize;
    }

    public Integer getXAxisIndex() {
        return xAxisIndex;
    }

    public Integer getYAxisIndex() {
        return yAxisIndex;
    }

    public String getStack() {
        return stack;
    }

    public Map<String, Object> getMarkPoint() {
        return markPoint;
    }

    public Map<String, Object> getItemStyle() {
        return itemStyle;
    }

    public Map<String, Object> getLineStyle() {
        return lineStyle;
    }

    public Map<String, Object> getAreaStyle() {
        return areaStyle;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Fluent builder for series configuration.
     */
    public static final class Builder {
        private final String type;
        private final String name;
        private final List<ChartPoint> data;
        private String color;
        private Boolean smooth;
        private Double lineWidth;
        private String symbol = "none";
        private Number symbolSize;
        private Integer xAxisIndex;
        private Integer yAxisIndex;
        private String stack;
        private final Map<String, Object> markPoint = new LinkedHashMap<String, Object>();
        private final Map<String, Object> itemStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> lineStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> areaStyle = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String type, String name, List<ChartPoint> data) {
            this.type = ValidationSupport.requireNonBlank(type, "type");
            this.name = name;
            this.data = ValidationSupport.mutableListCopy(data, "data");
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder smooth(Boolean smooth) {
            this.smooth = smooth;
            return this;
        }

        public Builder lineWidth(Double lineWidth) {
            this.lineWidth = ValidationSupport.requireNonNegativeNullable(lineWidth, "lineWidth");
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder symbolSize(Number symbolSize) {
            this.symbolSize = ValidationSupport.requireNonNegativeNullable(symbolSize, "symbolSize");
            return this;
        }

        public Builder xAxisIndex(Integer xAxisIndex) {
            this.xAxisIndex = ValidationSupport.requireNonNegativeNullable(xAxisIndex, "xAxisIndex");
            return this;
        }

        public Builder yAxisIndex(Integer yAxisIndex) {
            this.yAxisIndex = ValidationSupport.requireNonNegativeNullable(yAxisIndex, "yAxisIndex");
            return this;
        }

        public Builder stack(String stack) {
            this.stack = stack;
            return this;
        }

        public Builder markPoint(String key, Object value) {
            this.markPoint.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder itemStyle(String key, Object value) {
            this.itemStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder lineStyle(String key, Object value) {
            this.lineStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder areaStyle(String key, Object value) {
            this.areaStyle.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public SeriesSpec build() {
            return new SeriesSpec(type, name, data, color, smooth, lineWidth, symbol, symbolSize, xAxisIndex,
                    yAxisIndex, stack, markPoint, itemStyle, lineStyle, areaStyle, extensions);
        }
    }
}
