package io.github.echartsitext.option;

import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.BoxplotSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.FunnelSeriesSpec;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.RadarSeriesSpec;
import io.github.echartsitext.spec.SeriesSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the series array with theme-aware styling defaults.
 */
final class SeriesOptionFactory {
    List<Map<String, Object>> buildPieSeries(List<PieSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (PieSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildHeatmapSeries(List<HeatmapSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (HeatmapSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildRadarSeries(List<RadarSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (RadarSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildFunnelSeries(List<FunnelSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (FunnelSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildCandlestickSeries(List<CandlestickSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (CandlestickSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildBoxplotSeries(List<BoxplotSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (BoxplotSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildBar3DSeries(List<Bar3DSeriesSpec> seriesSpecs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Bar3DSeriesSpec spec : seriesSpecs) {
            result.add(spec.toOptionMap());
        }
        return result;
    }

    List<Map<String, Object>> buildSeries(List<SeriesSpec> seriesSpecs, ChartTheme theme) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < seriesSpecs.size(); i++) {
            SeriesSpec spec = seriesSpecs.get(i);
            OptionMapBuilder series = OptionMapBuilder.create()
                    .put("name", spec.getName())
                    .put("type", spec.getType())
                    .put("data", spec.getData().stream().map(OptionSupport::toPoint).collect(Collectors.toList()))
                    .putIfNotNull("symbol", spec.getSymbol())
                    .putIfNotNull("symbolSize", spec.getSymbolSize())
                    .putIfNotNull("smooth", spec.getSmooth())
                    .putIfNotNull("stack", spec.getStack())
                    .putIfNotNull("xAxisIndex", spec.getXAxisIndex())
                    .putIfNotNull("yAxisIndex", spec.getYAxisIndex());

            String color = spec.getColor() == null ? theme.paletteAt(i) : spec.getColor();
            if ("line".equals(spec.getType())) {
                LinkedHashMap<String, Object> lineStyle = new LinkedHashMap<String, Object>(spec.getLineStyle());
                if (!lineStyle.containsKey("width")) {
                    lineStyle.put("width", spec.getLineWidth() == null ? theme.getDefaultLineWidth() : spec.getLineWidth());
                }
                if (!lineStyle.containsKey("color")) {
                    lineStyle.put("color", color);
                }
                series.put("lineStyle", lineStyle);
            }

            LinkedHashMap<String, Object> itemStyle = new LinkedHashMap<String, Object>(spec.getItemStyle());
            if (!itemStyle.containsKey("color")) {
                itemStyle.put("color", color);
            }
            if (!itemStyle.isEmpty()) {
                series.put("itemStyle", itemStyle);
            }
            if (!spec.getMarkPoint().isEmpty()) {
                series.put("markPoint", spec.getMarkPoint());
            }
            if (!spec.getAreaStyle().isEmpty()) {
                series.put("areaStyle", spec.getAreaStyle());
            }
            series.putAll(spec.getExtensions());
            result.add(series.build());
        }
        return result;
    }
}
