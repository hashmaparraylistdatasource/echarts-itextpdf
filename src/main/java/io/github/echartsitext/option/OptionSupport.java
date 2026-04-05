package io.github.echartsitext.option;

import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.TextStyleSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared helper methods used by the option composition pipeline.
 */
final class OptionSupport {
    private OptionSupport() {
    }

    static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    static Map<String, Object> mergeAxisLabel(AxisSpec spec, ChartTheme theme) {
        LinkedHashMap<String, Object> axisLabel = new LinkedHashMap<String, Object>();
        axisLabel.put("color", theme.getTextColor());
        axisLabel.put("fontFamily", theme.getFontFamily());
        axisLabel.put("fontSize", theme.getDefaultFontSize());
        axisLabel.putAll(spec.getAxisLabel());
        return axisLabel;
    }

    static Map<String, Object> toTextStyle(TextStyleSpec spec, ChartTheme theme, boolean bold) {
        LinkedHashMap<String, Object> style = new LinkedHashMap<String, Object>();
        style.put("fontFamily", theme.getFontFamily());
        style.put("fontSize", theme.getDefaultFontSize());
        style.put("fontWeight", bold ? "600" : "400");
        style.put("color", theme.getTextColor());
        if (spec != null) {
            putIfNotNull(style, "fontFamily", spec.getFontFamily());
            putIfNotNull(style, "fontSize", spec.getFontSize());
            putIfNotNull(style, "fontWeight", spec.getFontWeight());
            putIfNotNull(style, "color", spec.getColor());
        }
        return style;
    }

    static List<Number> toPoint(ChartPoint point) {
        return Arrays.<Number>asList(point.getX(), point.getY());
    }
}
