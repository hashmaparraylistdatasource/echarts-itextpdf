package io.github.echartsitext.option;

import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.AxisTitleLayoutMode;
import io.github.echartsitext.spec.RangeMode;
import io.github.echartsitext.theme.ChartTheme;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds xAxis and yAxis blocks from the typed axis model.
 */
final class AxisOptionFactory {
    Map<String, Object> buildAxis(AxisSpec spec, ChartTheme theme, boolean horizontalAxis) {
        String effectiveNameLocation = resolveNameLocation(spec, horizontalAxis);
        OptionMapBuilder axis = OptionMapBuilder.create()
                .putIfNotNull("name", spec.getName())
                .putIfNotNull("type", spec.getType())
                .put("show", spec.isShow());
        if (spec.getRangeMode() == RangeMode.FIXED) {
            axis.putIfNotNull("min", spec.getMin())
                    .putIfNotNull("max", spec.getMax());
        }
        axis.putIfNotNull("interval", spec.getInterval())
                .putIfNotNull("splitNumber", spec.getSplitNumber());
        if (spec.isScale()) {
            axis.put("scale", Boolean.TRUE);
        }
        axis.putIfNotNull("position", spec.getPosition());
        if (OptionSupport.hasText(spec.getName())) {
            axis.putIfNotNull("nameLocation", effectiveNameLocation)
                    .putIfNotNull("nameGap", spec.getNameGap() == null
                            ? defaultNameGap(horizontalAxis, spec.getTitleLayoutMode())
                            : spec.getNameGap())
                    .putIfNotNull("nameRotate", spec.getNameRotate() == null
                            ? defaultNameRotate(horizontalAxis, spec.getTitleLayoutMode())
                            : spec.getNameRotate());
        }
        axis.put("axisLabel", buildAxisLabel(spec, theme, horizontalAxis, effectiveNameLocation))
                .put("nameTextStyle", buildNameTextStyle(spec, theme, horizontalAxis, effectiveNameLocation));
        if (!spec.getAxisLine().isEmpty()) {
            axis.put("axisLine", spec.getAxisLine());
        }
        if (!spec.getAxisTick().isEmpty()) {
            axis.put("axisTick", spec.getAxisTick());
        }

        Map<String, Object> minorTick = new LinkedHashMap<String, Object>(spec.getMinorTick());
        if (spec.getSplitNumber() != null && !minorTick.containsKey("splitNumber")) {
            minorTick.put("splitNumber", spec.getSplitNumber());
        }
        if (!minorTick.isEmpty() && !minorTick.containsKey("show")) {
            minorTick.put("show", Boolean.TRUE);
        }
        if (!minorTick.isEmpty()) {
            axis.put("minorTick", minorTick);
        }
        axis.putAll(spec.getExtensions());
        return axis.build();
    }

    List<Map<String, Object>> buildAxes(List<AxisSpec> axes, ChartTheme theme, boolean horizontalAxis) {
        return axes.stream().map(axis -> buildAxis(axis, theme, horizontalAxis)).collect(Collectors.toList());
    }

    String resolveNameLocation(AxisSpec spec, boolean horizontalAxis) {
        if (spec.getNameLocation() != null) {
            return spec.getNameLocation();
        }
        return defaultNameLocation(horizontalAxis, spec.getTitleLayoutMode());
    }

    private String defaultNameLocation(boolean horizontalAxis, AxisTitleLayoutMode layoutMode) {
        if (layoutMode == AxisTitleLayoutMode.MIDDLE_SAFE) {
            return "middle";
        }
        return "end";
    }

    private Integer defaultNameGap(boolean horizontalAxis, AxisTitleLayoutMode layoutMode) {
        if (layoutMode == AxisTitleLayoutMode.MIDDLE_SAFE) {
            return horizontalAxis ? Integer.valueOf(34) : Integer.valueOf(42);
        }
        return horizontalAxis ? Integer.valueOf(28) : Integer.valueOf(16);
    }

    private Integer defaultNameRotate(boolean horizontalAxis, AxisTitleLayoutMode layoutMode) {
        if (layoutMode == AxisTitleLayoutMode.MIDDLE_SAFE && !horizontalAxis) {
            return Integer.valueOf(90);
        }
        return Integer.valueOf(0);
    }

    private Map<String, Object> buildAxisLabel(AxisSpec spec, ChartTheme theme, boolean horizontalAxis, String effectiveNameLocation) {
        LinkedHashMap<String, Object> axisLabel = new LinkedHashMap<String, Object>(OptionSupport.mergeAxisLabel(spec, theme));
        if (!horizontalAxis || !OptionSupport.hasText(spec.getName())) {
            return axisLabel;
        }
        if (spec.getTitleLayoutMode() == AxisTitleLayoutMode.END_SAFE && "end".equalsIgnoreCase(effectiveNameLocation)) {
            if (!axisLabel.containsKey("alignMinLabel")) {
                axisLabel.put("alignMinLabel", "left");
            }
            if (!axisLabel.containsKey("alignMaxLabel")) {
                axisLabel.put("alignMaxLabel", "right");
            }
        }
        return axisLabel;
    }

    private Map<String, Object> buildNameTextStyle(AxisSpec spec, ChartTheme theme, boolean horizontalAxis, String effectiveNameLocation) {
        LinkedHashMap<String, Object> style = new LinkedHashMap<String, Object>(OptionSupport.toTextStyle(spec.getNameTextStyle(), theme, false));
        if (!OptionSupport.hasText(spec.getName())) {
            return style;
        }
        if ("end".equalsIgnoreCase(effectiveNameLocation)) {
            if (horizontalAxis) {
                style.put("align", "left");
                style.put("verticalAlign", "top");
            } else {
                style.put("align", "right");
                style.put("verticalAlign", "bottom");
            }
        } else if ("start".equalsIgnoreCase(effectiveNameLocation)) {
            if (horizontalAxis) {
                style.put("align", "right");
                style.put("verticalAlign", "top");
            } else {
                style.put("align", "left");
                style.put("verticalAlign", "top");
            }
        }
        return style;
    }
}
