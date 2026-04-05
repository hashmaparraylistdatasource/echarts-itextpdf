package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable radar coordinate definition.
 */
public final class RadarSpec {
    private final List<RadarIndicatorSpec> indicators;
    private final String shape;
    private final String radius;
    private final String centerX;
    private final String centerY;
    private final Integer splitNumber;
    private final Integer nameGap;
    private final Map<String, Object> extensions;

    RadarSpec(List<RadarIndicatorSpec> indicators, String shape, String radius, String centerX, String centerY,
              Integer splitNumber, Integer nameGap, Map<String, Object> extensions) {
        this.indicators = ValidationSupport.immutableListCopy(indicators, "indicators");
        this.shape = shape;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
        this.nameGap = ValidationSupport.requireNonNegativeNullable(nameGap, "nameGap");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<RadarIndicatorSpec> getIndicators() {
        return indicators;
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> radar = new LinkedHashMap<String, Object>();
        radar.put("indicator", buildIndicatorData());
        if (shape != null) {
            radar.put("shape", shape);
        }
        if (radius != null) {
            radar.put("radius", radius);
        }
        if (centerX != null || centerY != null) {
            ArrayList<String> center = new ArrayList<String>(2);
            center.add(centerX == null ? "50%" : centerX);
            center.add(centerY == null ? "50%" : centerY);
            radar.put("center", center);
        }
        if (splitNumber != null) {
            radar.put("splitNumber", splitNumber);
        }
        if (nameGap != null) {
            radar.put("nameGap", nameGap);
        }
        radar.putAll(extensions);
        return radar;
    }

    private List<Map<String, Object>> buildIndicatorData() {
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(indicators.size());
        for (RadarIndicatorSpec indicator : indicators) {
            data.add(indicator.toOptionMap());
        }
        return data;
    }

    /**
     * Fluent builder for radar coordinates.
     */
    public static final class Builder {
        private List<RadarIndicatorSpec> indicators = Collections.emptyList();
        private String shape = "polygon";
        private String radius = "65%";
        private String centerX = "50%";
        private String centerY = "55%";
        private Integer splitNumber = Integer.valueOf(5);
        private Integer nameGap;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder indicators(List<RadarIndicatorSpec> indicators) {
            this.indicators = ValidationSupport.mutableListCopy(indicators, "indicators");
            return this;
        }

        public Builder shape(String shape) {
            this.shape = shape;
            return this;
        }

        public Builder radius(String radius) {
            this.radius = radius;
            return this;
        }

        public Builder center(String centerX, String centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            return this;
        }

        public Builder splitNumber(Integer splitNumber) {
            this.splitNumber = ValidationSupport.requirePositiveNullable(splitNumber, "splitNumber");
            return this;
        }

        public Builder nameGap(Integer nameGap) {
            this.nameGap = ValidationSupport.requireNonNegativeNullable(nameGap, "nameGap");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public RadarSpec build() {
            return new RadarSpec(indicators, shape, radius, centerX, centerY, splitNumber, nameGap, extensions);
        }
    }
}
