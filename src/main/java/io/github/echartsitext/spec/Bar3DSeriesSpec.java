package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed 3D bar series used by the first-class 3D builder.
 */
public final class Bar3DSeriesSpec {
    private final String name;
    private final List<ChartPoint3D> data;
    private final String shading;
    private final Double bevelSize;
    private final Boolean labelShow;
    private final Boolean emphasisLabelShow;
    private final Integer emphasisLabelFontSize;
    private final Map<String, Object> extensions;

    Bar3DSeriesSpec(String name, List<ChartPoint3D> data, String shading, Double bevelSize,
                    Boolean labelShow, Boolean emphasisLabelShow, Integer emphasisLabelFontSize,
                    Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.shading = ValidationSupport.requireNonBlank(shading, "shading");
        this.bevelSize = ValidationSupport.requireNonNegativeNullable(bevelSize, "bevelSize");
        this.labelShow = labelShow;
        this.emphasisLabelShow = emphasisLabelShow;
        this.emphasisLabelFontSize = ValidationSupport.requirePositiveNullable(emphasisLabelFontSize, "emphasisLabelFontSize");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "bar3D");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }

        List<List<Number>> points = new ArrayList<List<Number>>(data.size());
        for (ChartPoint3D point : data) {
            points.add(asList(point));
        }
        series.put("data", points);
        series.put("shading", shading);
        if (bevelSize != null) {
            series.put("bevelSize", bevelSize);
        }
        if (labelShow != null) {
            series.put("label", singleton("show", labelShow));
        }
        if (emphasisLabelShow != null || emphasisLabelFontSize != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            if (emphasisLabelShow != null) {
                label.put("show", emphasisLabelShow);
            }
            if (emphasisLabelFontSize != null) {
                label.put("fontSize", emphasisLabelFontSize);
            }
            series.put("emphasis", singleton("label", label));
        }
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for a typed bar3D series.
     */
    public static final class Builder {
        private String name;
        private List<ChartPoint3D> data = Collections.emptyList();
        private String shading = "lambert";
        private Double bevelSize = Double.valueOf(0.15d);
        private Boolean labelShow = Boolean.FALSE;
        private Boolean emphasisLabelShow = Boolean.TRUE;
        private Integer emphasisLabelFontSize = Integer.valueOf(12);
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<ChartPoint3D> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder shading(String shading) {
            this.shading = ValidationSupport.requireNonBlank(shading, "shading");
            return this;
        }

        public Builder bevelSize(Double bevelSize) {
            this.bevelSize = ValidationSupport.requireNonNegativeNullable(bevelSize, "bevelSize");
            return this;
        }

        public Builder labelShow(Boolean labelShow) {
            this.labelShow = labelShow;
            return this;
        }

        public Builder emphasisLabel(Boolean show, Integer fontSize) {
            this.emphasisLabelShow = show;
            this.emphasisLabelFontSize = ValidationSupport.requirePositiveNullable(fontSize, "emphasisLabelFontSize");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Bar3DSeriesSpec build() {
            return new Bar3DSeriesSpec(name, data, shading, bevelSize, labelShow, emphasisLabelShow,
                    emphasisLabelFontSize, extensions);
        }
    }

    private static List<Number> asList(ChartPoint3D point) {
        ArrayList<Number> values = new ArrayList<Number>(3);
        values.add(point.getX());
        values.add(point.getY());
        values.add(point.getZ());
        return values;
    }

    private static Map<String, Object> singleton(String key, Object value) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(key, value);
        return map;
    }
}
