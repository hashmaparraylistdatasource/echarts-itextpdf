package io.github.echartsitext.module;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds a top-level visualMap configuration for continuous or piecewise value mapping.
 */
public final class VisualMapModule implements OptionModule {
    private final String type;
    private final Number min;
    private final Number max;
    private final Integer dimension;
    private final Integer seriesIndex;
    private final Boolean calculable;
    private final String orient;
    private final String left;
    private final String top;
    private final List<Map<String, Object>> pieces;
    private final List<String> text;
    private final Map<String, Object> inRange;
    private final Map<String, Object> outOfRange;
    private final Map<String, Object> extensions;

    private VisualMapModule(String type, Number min, Number max, Integer dimension, Integer seriesIndex,
                            Boolean calculable, String orient, String left, String top,
                            List<Map<String, Object>> pieces, List<String> text,
                            Map<String, Object> inRange, Map<String, Object> outOfRange,
                            Map<String, Object> extensions) {
        this.type = ValidationSupport.requireNonBlank(type, "type");
        this.min = min;
        this.max = max;
        if (this.min != null && this.max != null && this.min.doubleValue() > this.max.doubleValue()) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.dimension = ValidationSupport.requireNonNegativeNullable(dimension, "dimension");
        this.seriesIndex = ValidationSupport.requireNonNegativeNullable(seriesIndex, "seriesIndex");
        this.calculable = calculable;
        this.orient = orient;
        this.left = left;
        this.top = top;
        this.pieces = copyPieces(pieces);
        this.text = copyText(text);
        this.inRange = ValidationSupport.immutableMapCopy(inRange, "inRange");
        this.outOfRange = ValidationSupport.immutableMapCopy(outOfRange, "outOfRange");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder continuous(Number min, Number max) {
        return new Builder("continuous").min(min).max(max);
    }

    public static Builder piecewise() {
        return new Builder("piecewise");
    }

    @Override
    public void apply(OptionTarget target, ModuleContext context) {
        LinkedHashMap<String, Object> visualMap = new LinkedHashMap<String, Object>();
        visualMap.put("type", type);
        putIfNotNull(visualMap, "min", min);
        putIfNotNull(visualMap, "max", max);
        putIfNotNull(visualMap, "dimension", dimension);
        putIfNotNull(visualMap, "seriesIndex", seriesIndex);
        putIfNotNull(visualMap, "calculable", calculable);
        putIfNotNull(visualMap, "orient", orient);
        putIfNotNull(visualMap, "left", left);
        putIfNotNull(visualMap, "top", top);
        if (!pieces.isEmpty()) {
            visualMap.put("pieces", new ArrayList<Map<String, Object>>(pieces));
        }
        if (!text.isEmpty()) {
            visualMap.put("text", new ArrayList<String>(text));
        }
        if (!inRange.isEmpty()) {
            visualMap.put("inRange", new LinkedHashMap<String, Object>(inRange));
        }
        if (!outOfRange.isEmpty()) {
            visualMap.put("outOfRange", new LinkedHashMap<String, Object>(outOfRange));
        }
        visualMap.putAll(extensions);
        target.appendToList("visualMap", visualMap);
    }

    private void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    /**
     * Builder for visualMap so callers can configure the common dimensions without dropping to raw JSON.
     */
    public static final class Builder {
        private final String type;
        private Number min;
        private Number max;
        private Integer dimension;
        private Integer seriesIndex;
        private Boolean calculable;
        private String orient;
        private String left;
        private String top;
        private final List<Map<String, Object>> pieces = new ArrayList<Map<String, Object>>();
        private final List<String> text = new ArrayList<String>();
        private final Map<String, Object> inRange = new LinkedHashMap<String, Object>();
        private final Map<String, Object> outOfRange = new LinkedHashMap<String, Object>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(String type) {
            this.type = ValidationSupport.requireNonBlank(type, "type");
        }

        public Builder min(Number min) {
            this.min = min;
            return this;
        }

        public Builder max(Number max) {
            this.max = max;
            return this;
        }

        public Builder dimension(Integer dimension) {
            this.dimension = ValidationSupport.requireNonNegativeNullable(dimension, "dimension");
            return this;
        }

        public Builder seriesIndex(Integer seriesIndex) {
            this.seriesIndex = ValidationSupport.requireNonNegativeNullable(seriesIndex, "seriesIndex");
            return this;
        }

        public Builder calculable(Boolean calculable) {
            this.calculable = calculable;
            return this;
        }

        public Builder orient(String orient) {
            this.orient = orient;
            return this;
        }

        public Builder left(String left) {
            this.left = left;
            return this;
        }

        public Builder top(String top) {
            this.top = top;
            return this;
        }

        public Builder text(String low, String high) {
            this.text.clear();
            this.text.addAll(copyText(Arrays.asList(low, high)));
            return this;
        }

        public Builder piece(Map<String, Object> piece) {
            this.pieces.add(ValidationSupport.mutableMapCopy(piece, "piece"));
            return this;
        }

        public Builder inRange(String key, Object value) {
            this.inRange.put(ValidationSupport.requireNonBlank(key, "key"), ValidationSupport.deepCopyValue(value));
            return this;
        }

        public Builder outOfRange(String key, Object value) {
            this.outOfRange.put(ValidationSupport.requireNonBlank(key, "key"), ValidationSupport.deepCopyValue(value));
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), ValidationSupport.deepCopyValue(value));
            return this;
        }

        public VisualMapModule build() {
            return new VisualMapModule(type, min, max, dimension, seriesIndex, calculable, orient, left, top,
                    pieces, text, inRange, outOfRange, extensions);
        }
    }

    private static List<Map<String, Object>> copyPieces(List<Map<String, Object>> pieces) {
        ArrayList<Map<String, Object>> copy = new ArrayList<Map<String, Object>>(pieces.size());
        for (int i = 0; i < pieces.size(); i++) {
            Map<String, Object> piece = pieces.get(i);
            if (piece == null) {
                throw new NullPointerException("pieces[" + i + "]");
            }
            copy.add(ValidationSupport.immutableMapCopy(piece, "pieces[" + i + "]"));
        }
        return copy;
    }

    private static List<String> copyText(List<String> text) {
        ArrayList<String> copy = new ArrayList<String>(text.size());
        for (int i = 0; i < text.size(); i++) {
            String item = text.get(i);
            if (item == null) {
                throw new NullPointerException("text[" + i + "]");
            }
            copy.add(item);
        }
        return copy;
    }
}
