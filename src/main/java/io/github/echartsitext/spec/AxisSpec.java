package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Describes one ECharts axis, including range behavior and visual overrides.
 */
public final class AxisSpec {
    private final String name;
    private final String type;
    private final Double min;
    private final Double max;
    private final Double interval;
    private final Integer splitNumber;
    private final boolean show;
    private final boolean scale;
    private final String position;
    private final AxisTitleLayoutMode titleLayoutMode;
    private final String nameLocation;
    private final Integer nameGap;
    private final Integer nameRotate;
    private final RangeMode rangeMode;
    private final TextStyleSpec nameTextStyle;
    private final Map<String, Object> axisLabel;
    private final Map<String, Object> axisLine;
    private final Map<String, Object> axisTick;
    private final Map<String, Object> minorTick;
    private final Map<String, Object> extensions;

    AxisSpec(String name, String type, Double min, Double max, Double interval, Integer splitNumber, boolean show,
             boolean scale, String position, AxisTitleLayoutMode titleLayoutMode,
             String nameLocation, Integer nameGap, Integer nameRotate,
             RangeMode rangeMode, TextStyleSpec nameTextStyle,
             Map<String, Object> axisLabel, Map<String, Object> axisLine, Map<String, Object> axisTick,
             Map<String, Object> minorTick, Map<String, Object> extensions) {
        this.name = name;
        this.type = ValidationSupport.requireNonBlank(type, "type");
        this.min = min;
        this.max = max;
        this.interval = ValidationSupport.requirePositiveNullable(interval, "interval");
        this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
        this.show = show;
        this.scale = scale;
        this.position = position;
        this.titleLayoutMode = Objects.requireNonNull(titleLayoutMode, "titleLayoutMode");
        this.nameLocation = nameLocation;
        this.nameGap = ValidationSupport.requireNonNegativeNullable(nameGap, "nameGap");
        this.nameRotate = nameRotate;
        this.rangeMode = Objects.requireNonNull(rangeMode, "rangeMode");
        this.nameTextStyle = nameTextStyle;
        this.axisLabel = axisLabel == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(axisLabel, "axisLabel");
        this.axisLine = axisLine == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(axisLine, "axisLine");
        this.axisTick = axisTick == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(axisTick, "axisTick");
        this.minorTick = minorTick == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(minorTick, "minorTick");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getInterval() {
        return interval;
    }

    public Integer getSplitNumber() {
        return splitNumber;
    }

    public boolean isShow() {
        return show;
    }

    public boolean isScale() {
        return scale;
    }

    public String getPosition() {
        return position;
    }

    public AxisTitleLayoutMode getTitleLayoutMode() {
        return titleLayoutMode;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public Integer getNameGap() {
        return nameGap;
    }

    public Integer getNameRotate() {
        return nameRotate;
    }

    public RangeMode getRangeMode() {
        return rangeMode;
    }

    public TextStyleSpec getNameTextStyle() {
        return nameTextStyle;
    }

    public Map<String, Object> getAxisLabel() {
        return axisLabel;
    }

    public Map<String, Object> getAxisLine() {
        return axisLine;
    }

    public Map<String, Object> getAxisTick() {
        return axisTick;
    }

    public Map<String, Object> getMinorTick() {
        return minorTick;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Fluent builder for axis configuration.
     */
    public static final class Builder {
        private String name = "";
        private String type = "value";
        private Double min;
        private Double max;
        private Double interval;
        private Integer splitNumber;
        private boolean show = true;
        private boolean scale;
        private String position;
        private AxisTitleLayoutMode titleLayoutMode = AxisTitleLayoutMode.END_SAFE;
        private String nameLocation;
        private Integer nameGap;
        private Integer nameRotate;
        private RangeMode rangeMode = RangeMode.AUTO;
        private TextStyleSpec nameTextStyle;
        private final Map<String, Object> axisLabel = new LinkedHashMap<String, Object>();
        private final Map<String, Object> axisLine = new LinkedHashMap<String, Object>();
        private final Map<String, Object> axisTick = new LinkedHashMap<String, Object>();
        private final Map<String, Object> minorTick = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = ValidationSupport.requireNonBlank(type, "type");
            return this;
        }

        public Builder range(Double min, Double max) {
            this.rangeMode = RangeMode.FIXED;
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder autoRange() {
            this.rangeMode = RangeMode.AUTO;
            this.min = null;
            this.max = null;
            return this;
        }

        public Builder interval(Double interval) {
            this.interval = ValidationSupport.requirePositiveNullable(interval, "interval");
            return this;
        }

        public Builder splitNumber(Integer splitNumber) {
            this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
            return this;
        }

        public Builder show(boolean show) {
            this.show = show;
            return this;
        }

        public Builder scale(boolean scale) {
            this.scale = scale;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder titleLayoutMode(AxisTitleLayoutMode titleLayoutMode) {
            this.titleLayoutMode = Objects.requireNonNull(titleLayoutMode, "titleLayoutMode");
            return this;
        }

        public Builder nameLocation(String nameLocation) {
            this.nameLocation = nameLocation;
            return this;
        }

        public Builder nameGap(Integer nameGap) {
            this.nameGap = ValidationSupport.requireNonNegativeNullable(nameGap, "nameGap");
            return this;
        }

        public Builder nameRotate(Integer nameRotate) {
            this.nameRotate = nameRotate;
            return this;
        }

        public Builder nameTextStyle(TextStyleSpec nameTextStyle) {
            this.nameTextStyle = nameTextStyle;
            return this;
        }

        public Builder axisLabel(String key, Object value) {
            this.axisLabel.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder categories(List<?> values) {
            this.extensions.put("data", new ArrayList<Object>(ValidationSupport.mutableObjectListCopy(values, "values")));
            return this;
        }

        public Builder axisLine(String key, Object value) {
            this.axisLine.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder axisTick(String key, Object value) {
            this.axisTick.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder minorTick(String key, Object value) {
            this.minorTick.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public AxisSpec build() {
            return new AxisSpec(name, type, min, max, interval, splitNumber, show, scale, position,
                    titleLayoutMode,
                    nameLocation, nameGap, nameRotate, rangeMode,
                    nameTextStyle, axisLabel, axisLine, axisTick, minorTick, extensions);
        }
    }
}
