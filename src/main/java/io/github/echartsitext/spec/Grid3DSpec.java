package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable 3D grid definition used by typed 3D chart builders.
 */
public final class Grid3DSpec {
    private final Number boxWidth;
    private final Number boxDepth;
    private final Number alpha;
    private final Number beta;
    private final Double mainLightIntensity;
    private final Boolean mainLightShadow;
    private final Double ambientLightIntensity;
    private final Map<String, Object> extensions;

    Grid3DSpec(Number boxWidth, Number boxDepth, Number alpha, Number beta,
               Double mainLightIntensity, Boolean mainLightShadow,
               Double ambientLightIntensity, Map<String, Object> extensions) {
        this.boxWidth = positive(boxWidth, "boxWidth");
        this.boxDepth = positive(boxDepth, "boxDepth");
        this.alpha = alpha;
        this.beta = beta;
        this.mainLightIntensity = ValidationSupport.requirePositiveNullable(mainLightIntensity, "mainLightIntensity");
        this.mainLightShadow = mainLightShadow;
        this.ambientLightIntensity = ValidationSupport.requireNonNegativeNullable(ambientLightIntensity, "ambientLightIntensity");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> toOptionMap() {
        LinkedHashMap<String, Object> grid = new LinkedHashMap<String, Object>();
        if (boxWidth != null) {
            grid.put("boxWidth", boxWidth);
        }
        if (boxDepth != null) {
            grid.put("boxDepth", boxDepth);
        }

        LinkedHashMap<String, Object> viewControl = new LinkedHashMap<String, Object>();
        if (alpha != null) {
            viewControl.put("alpha", alpha);
        }
        if (beta != null) {
            viewControl.put("beta", beta);
        }
        if (!viewControl.isEmpty()) {
            grid.put("viewControl", viewControl);
        }

        LinkedHashMap<String, Object> light = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> main = new LinkedHashMap<String, Object>();
        if (mainLightIntensity != null) {
            main.put("intensity", mainLightIntensity);
        }
        if (mainLightShadow != null) {
            main.put("shadow", mainLightShadow);
        }
        if (!main.isEmpty()) {
            light.put("main", main);
        }
        LinkedHashMap<String, Object> ambient = new LinkedHashMap<String, Object>();
        if (ambientLightIntensity != null) {
            ambient.put("intensity", ambientLightIntensity);
        }
        if (!ambient.isEmpty()) {
            light.put("ambient", ambient);
        }
        if (!light.isEmpty()) {
            grid.put("light", light);
        }

        grid.putAll(extensions);
        return grid;
    }

    /**
     * Fluent builder for 3D grid configuration.
     */
    public static final class Builder {
        private Number boxWidth = Integer.valueOf(110);
        private Number boxDepth = Integer.valueOf(90);
        private Number alpha = Integer.valueOf(22);
        private Number beta = Integer.valueOf(32);
        private Double mainLightIntensity = Double.valueOf(1.1d);
        private Boolean mainLightShadow = Boolean.TRUE;
        private Double ambientLightIntensity = Double.valueOf(0.45d);
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder boxSize(Number boxWidth, Number boxDepth) {
            this.boxWidth = positive(boxWidth, "boxWidth");
            this.boxDepth = positive(boxDepth, "boxDepth");
            return this;
        }

        public Builder viewAngles(Number alpha, Number beta) {
            this.alpha = alpha;
            this.beta = beta;
            return this;
        }

        public Builder mainLight(Double intensity, Boolean shadow) {
            this.mainLightIntensity = ValidationSupport.requirePositiveNullable(intensity, "mainLightIntensity");
            this.mainLightShadow = shadow;
            return this;
        }

        public Builder ambientLight(Double intensity) {
            this.ambientLightIntensity = ValidationSupport.requireNonNegativeNullable(intensity, "ambientLightIntensity");
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public Grid3DSpec build() {
            return new Grid3DSpec(boxWidth, boxDepth, alpha, beta, mainLightIntensity, mainLightShadow,
                    ambientLightIntensity, extensions);
        }

    }

    private static Number positive(Number value, String name) {
        if (value == null) {
            return null;
        }
        if (value.doubleValue() <= 0d) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return value;
    }
}
