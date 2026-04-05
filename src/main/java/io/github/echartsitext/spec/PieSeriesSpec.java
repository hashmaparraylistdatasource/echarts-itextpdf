package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable typed pie series used by the first-class pie builder.
 */
public final class PieSeriesSpec {
    private final String name;
    private final List<PieSliceSpec> data;
    private final String radius;
    private final String innerRadius;
    private final String outerRadius;
    private final String centerX;
    private final String centerY;
    private final String roseType;
    private final Boolean avoidLabelOverlap;
    private final Boolean labelShow;
    private final Map<String, Object> extensions;

    PieSeriesSpec(String name, List<PieSliceSpec> data, String radius, String innerRadius, String outerRadius,
                  String centerX, String centerY, String roseType, Boolean avoidLabelOverlap,
                  Boolean labelShow, Map<String, Object> extensions) {
        this.name = name;
        this.data = ValidationSupport.immutableListCopy(data, "data");
        this.radius = radius;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.roseType = roseType;
        this.avoidLabelOverlap = avoidLabelOverlap;
        this.labelShow = labelShow;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<PieSliceSpec> getData() {
        return data;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
        series.put("type", "pie");
        if (name != null && name.trim().length() > 0) {
            series.put("name", name);
        }
        if (radius != null) {
            series.put("radius", radius);
        } else if (innerRadius != null || outerRadius != null) {
            ArrayList<String> radiusValues = new ArrayList<String>(2);
            radiusValues.add(innerRadius == null ? "0%" : innerRadius);
            radiusValues.add(outerRadius == null ? "75%" : outerRadius);
            series.put("radius", radiusValues);
        }
        if (centerX != null || centerY != null) {
            ArrayList<String> centerValues = new ArrayList<String>(2);
            centerValues.add(centerX == null ? "50%" : centerX);
            centerValues.add(centerY == null ? "50%" : centerY);
            series.put("center", centerValues);
        }
        if (roseType != null) {
            series.put("roseType", roseType);
        }
        if (avoidLabelOverlap != null) {
            series.put("avoidLabelOverlap", avoidLabelOverlap);
        }
        if (labelShow != null) {
            LinkedHashMap<String, Object> label = new LinkedHashMap<String, Object>();
            label.put("show", labelShow);
            series.put("label", label);
        }
        ArrayList<Map<String, Object>> slices = new ArrayList<Map<String, Object>>(data.size());
        for (PieSliceSpec slice : data) {
            slices.add(slice.toOptionMap());
        }
        series.put("data", slices);
        series.putAll(extensions);
        return series;
    }

    /**
     * Fluent builder for pie-series configuration.
     */
    public static final class Builder {
        private String name;
        private List<PieSliceSpec> data = Collections.emptyList();
        private String radius = "70%";
        private String innerRadius;
        private String outerRadius;
        private String centerX = "50%";
        private String centerY = "55%";
        private String roseType;
        private Boolean avoidLabelOverlap = Boolean.TRUE;
        private Boolean labelShow = Boolean.TRUE;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(List<PieSliceSpec> data) {
            this.data = ValidationSupport.mutableListCopy(data, "data");
            return this;
        }

        public Builder radius(String radius) {
            this.radius = radius;
            this.innerRadius = null;
            this.outerRadius = null;
            return this;
        }

        public Builder donut(String innerRadius, String outerRadius) {
            this.radius = null;
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;
            return this;
        }

        public Builder center(String centerX, String centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            return this;
        }

        public Builder roseType(String roseType) {
            this.roseType = roseType;
            return this;
        }

        public Builder avoidLabelOverlap(Boolean avoidLabelOverlap) {
            this.avoidLabelOverlap = avoidLabelOverlap;
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

        public PieSeriesSpec build() {
            return new PieSeriesSpec(name, data, radius, innerRadius, outerRadius, centerX, centerY,
                    roseType, avoidLabelOverlap, labelShow, extensions);
        }
    }
}
