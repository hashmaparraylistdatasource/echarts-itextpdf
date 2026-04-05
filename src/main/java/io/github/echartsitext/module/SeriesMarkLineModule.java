package io.github.echartsitext.module;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds a markLine block to a target series so reference thresholds stay reusable and strongly typed.
 */
public final class SeriesMarkLineModule implements OptionModule {
    private final int seriesIndex;
    private final Map<String, Object> dataItem;
    private final Map<String, Object> extensions;

    private SeriesMarkLineModule(int seriesIndex, Map<String, Object> dataItem, Map<String, Object> extensions) {
        this.seriesIndex = ValidationSupport.requireNonNegativeNullable(Integer.valueOf(seriesIndex), "seriesIndex").intValue();
        this.dataItem = ValidationSupport.immutableMapCopy(dataItem, "dataItem");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static SeriesMarkLineModule horizontalLine(int seriesIndex, String name, Number yAxis) {
        LinkedHashMap<String, Object> dataItem = new LinkedHashMap<String, Object>();
        dataItem.put("name", ValidationSupport.requireNonBlank(name, "name"));
        dataItem.put("yAxis", java.util.Objects.requireNonNull(yAxis, "yAxis"));
        return new SeriesMarkLineModule(seriesIndex, dataItem, new LinkedHashMap<String, Object>());
    }

    public static SeriesMarkLineModule verticalLine(int seriesIndex, String name, Number xAxis) {
        LinkedHashMap<String, Object> dataItem = new LinkedHashMap<String, Object>();
        dataItem.put("name", ValidationSupport.requireNonBlank(name, "name"));
        dataItem.put("xAxis", java.util.Objects.requireNonNull(xAxis, "xAxis"));
        return new SeriesMarkLineModule(seriesIndex, dataItem, new LinkedHashMap<String, Object>());
    }

    @Override
    public void apply(OptionTarget target, ModuleContext context) {
        List<Map<String, Object>> seriesList = target.ensureObjectList("series");
        if (seriesIndex < 0 || seriesIndex >= seriesList.size()) {
            throw new IllegalStateException("Series index " + seriesIndex + " is out of range");
        }

        Map<String, Object> series = seriesList.get(seriesIndex);
        Map<String, Object> markLine = OptionTargets.ensureMap(series, "markLine");
        if (!markLine.containsKey("symbol")) {
            markLine.put("symbol", Arrays.asList("none", "none"));
        }
        List<Object> data = OptionTargets.ensureList(markLine, "data");
        data.add(new LinkedHashMap<String, Object>(dataItem));
        markLine.putAll(extensions);
    }
}
