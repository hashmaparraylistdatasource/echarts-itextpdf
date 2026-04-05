package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The immutable top-level chart model consumed by the option writer and renderers.
 */
public final class ChartSpec {
    private final ChartType chartType;
    private final int width;
    private final int height;
    private final String backgroundColor;
    private final ChartTheme theme;
    private final TitleSpec title;
    private final LegendSpec legend;
    private final TooltipSpec tooltip;
    private final GridSpec grid;
    private final RadarSpec radar;
    private final Axis3DSpec xAxis3D;
    private final Axis3DSpec yAxis3D;
    private final Axis3DSpec zAxis3D;
    private final Grid3DSpec grid3D;
    private final List<AxisSpec> xAxes;
    private final List<AxisSpec> yAxes;
    private final List<SeriesSpec> series;
    private final List<PieSeriesSpec> pieSeries;
    private final List<RadarSeriesSpec> radarSeries;
    private final List<FunnelSeriesSpec> funnelSeries;
    private final List<TreeSeriesSpec> treeSeries;
    private final List<TreemapSeriesSpec> treemapSeries;
    private final List<SunburstSeriesSpec> sunburstSeries;
    private final List<BoxplotSeriesSpec> boxplotSeries;
    private final List<HeatmapSeriesSpec> heatmapSeries;
    private final List<CandlestickSeriesSpec> candlestickSeries;
    private final List<Bar3DSeriesSpec> bar3DSeries;
    private final List<OptionModule> modules;
    private final Map<String, Object> extensions;

    ChartSpec(ChartType chartType, int width, int height, String backgroundColor, ChartTheme theme,
              TitleSpec title, LegendSpec legend, TooltipSpec tooltip, GridSpec grid, RadarSpec radar,
              Axis3DSpec xAxis3D, Axis3DSpec yAxis3D, Axis3DSpec zAxis3D, Grid3DSpec grid3D,
              List<AxisSpec> xAxes, List<AxisSpec> yAxes, List<SeriesSpec> series,
              List<PieSeriesSpec> pieSeries, List<RadarSeriesSpec> radarSeries, List<FunnelSeriesSpec> funnelSeries,
              List<TreeSeriesSpec> treeSeries,
              List<TreemapSeriesSpec> treemapSeries, List<SunburstSeriesSpec> sunburstSeries,
              List<BoxplotSeriesSpec> boxplotSeries,
              List<HeatmapSeriesSpec> heatmapSeries,
              List<CandlestickSeriesSpec> candlestickSeries, List<Bar3DSeriesSpec> bar3DSeries,
              List<OptionModule> modules, Map<String, Object> extensions) {
        this.chartType = Objects.requireNonNull(chartType, "chartType");
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        this.backgroundColor = backgroundColor;
        this.theme = theme;
        this.title = title;
        this.legend = legend;
        this.tooltip = tooltip;
        this.grid = grid;
        this.radar = radar;
        this.xAxis3D = xAxis3D;
        this.yAxis3D = yAxis3D;
        this.zAxis3D = zAxis3D;
        this.grid3D = grid3D;
        this.xAxes = ValidationSupport.immutableListCopy(xAxes, "xAxes");
        this.yAxes = ValidationSupport.immutableListCopy(yAxes, "yAxes");
        this.series = ValidationSupport.immutableListCopy(series, "series");
        this.pieSeries = ValidationSupport.immutableListCopy(pieSeries, "pieSeries");
        this.radarSeries = ValidationSupport.immutableListCopy(radarSeries, "radarSeries");
        this.funnelSeries = ValidationSupport.immutableListCopy(funnelSeries, "funnelSeries");
        this.treeSeries = ValidationSupport.immutableListCopy(treeSeries, "treeSeries");
        this.treemapSeries = ValidationSupport.immutableListCopy(treemapSeries, "treemapSeries");
        this.sunburstSeries = ValidationSupport.immutableListCopy(sunburstSeries, "sunburstSeries");
        this.boxplotSeries = ValidationSupport.immutableListCopy(boxplotSeries, "boxplotSeries");
        this.heatmapSeries = ValidationSupport.immutableListCopy(heatmapSeries, "heatmapSeries");
        this.candlestickSeries = ValidationSupport.immutableListCopy(candlestickSeries, "candlestickSeries");
        this.bar3DSeries = ValidationSupport.immutableListCopy(bar3DSeries, "bar3DSeries");
        this.modules = ValidationSupport.immutableListCopy(modules, "modules");
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
        validateModelShape();
    }

    public static Builder builder(ChartType chartType) {
        return new Builder(chartType);
    }

    public ChartType getChartType() {
        return chartType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public ChartTheme getTheme() {
        return theme;
    }

    public TitleSpec getTitle() {
        return title;
    }

    public LegendSpec getLegend() {
        return legend;
    }

    public TooltipSpec getTooltip() {
        return tooltip;
    }

    public GridSpec getGrid() {
        return grid;
    }

    public RadarSpec getRadar() {
        return radar;
    }

    public Axis3DSpec getXAxis3D() {
        return xAxis3D;
    }

    public Axis3DSpec getYAxis3D() {
        return yAxis3D;
    }

    public Axis3DSpec getZAxis3D() {
        return zAxis3D;
    }

    public Grid3DSpec getGrid3D() {
        return grid3D;
    }

    public List<AxisSpec> getXAxes() {
        return xAxes;
    }

    public List<AxisSpec> getYAxes() {
        return yAxes;
    }

    public List<SeriesSpec> getSeries() {
        return series;
    }

    public List<PieSeriesSpec> getPieSeries() {
        return pieSeries;
    }

    public List<RadarSeriesSpec> getRadarSeries() {
        return radarSeries;
    }

    public List<FunnelSeriesSpec> getFunnelSeries() {
        return funnelSeries;
    }

    public List<TreeSeriesSpec> getTreeSeries() {
        return treeSeries;
    }

    public List<TreemapSeriesSpec> getTreemapSeries() {
        return treemapSeries;
    }

    public List<SunburstSeriesSpec> getSunburstSeries() {
        return sunburstSeries;
    }

    public List<BoxplotSeriesSpec> getBoxplotSeries() {
        return boxplotSeries;
    }

    public List<HeatmapSeriesSpec> getHeatmapSeries() {
        return heatmapSeries;
    }

    public List<CandlestickSeriesSpec> getCandlestickSeries() {
        return candlestickSeries;
    }

    public List<Bar3DSeriesSpec> getBar3DSeries() {
        return bar3DSeries;
    }

    public List<OptionModule> getModules() {
        return modules;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    private void validateModelShape() {
        if (chartType.isThreeDimensional()) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty() || radar != null) {
                throw new IllegalArgumentException("Three-dimensional chart types must not define 2D axes or non-3D series");
            }
            if (xAxis3D == null || yAxis3D == null || zAxis3D == null) {
                throw new IllegalArgumentException("Three-dimensional chart types require xAxis3D, yAxis3D, and zAxis3D");
            }
            if (bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Three-dimensional chart types require at least one typed 3D series");
            }
            return;
        }
        if (chartType == ChartType.PIE) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Pie charts must only define pie series");
            }
            if (pieSeries.isEmpty()) {
                throw new IllegalArgumentException("Pie charts require at least one typed pie series");
            }
            return;
        }
        if (chartType == ChartType.RADAR) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty()
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Radar charts must only define a typed radar coordinate and radar series");
            }
            if (radar == null || radarSeries.isEmpty()) {
                throw new IllegalArgumentException("Radar charts require radar and at least one typed radar series");
            }
            return;
        }
        if (chartType == ChartType.FUNNEL) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !radarSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Funnel charts must only define typed funnel series");
            }
            if (!funnelSeries.isEmpty()) {
                return;
            }
            throw new IllegalArgumentException("Funnel charts require at least one typed funnel series");
        }
        if (chartType == ChartType.TREE) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty() || !boxplotSeries.isEmpty()
                    || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Tree charts must only define typed tree series");
            }
            if (treeSeries.isEmpty()) {
                throw new IllegalArgumentException("Tree charts require at least one typed tree series");
            }
            return;
        }
        if (chartType == ChartType.TREEMAP) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !sunburstSeries.isEmpty() || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty()
                    || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Treemap charts must only define typed treemap series");
            }
            if (treemapSeries.isEmpty()) {
                throw new IllegalArgumentException("Treemap charts require at least one typed treemap series");
            }
            return;
        }
        if (chartType == ChartType.SUNBURST) {
            if (!xAxes.isEmpty() || !yAxes.isEmpty() || !series.isEmpty()
                    || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty()
                    || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Sunburst charts must only define typed sunburst series");
            }
            if (sunburstSeries.isEmpty()) {
                throw new IllegalArgumentException("Sunburst charts require at least one typed sunburst series");
            }
            return;
        }
        if (chartType == ChartType.BOXPLOT) {
            if (!series.isEmpty() || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Boxplot charts must only define 2D axes and typed boxplot series");
            }
            if (xAxes.isEmpty() || yAxes.isEmpty() || boxplotSeries.isEmpty()) {
                throw new IllegalArgumentException("Boxplot charts require xAxis, yAxis, and at least one typed boxplot series");
            }
            return;
        }
        if (chartType == ChartType.HEATMAP) {
            if (!series.isEmpty() || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty()
                    || !candlestickSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Heatmap charts must only define 2D axes and typed heatmap series");
            }
            if (xAxes.isEmpty() || yAxes.isEmpty() || heatmapSeries.isEmpty()) {
                throw new IllegalArgumentException("Heatmap charts require xAxis, yAxis, and at least one heatmap series");
            }
            return;
        }
        if (chartType == ChartType.CANDLESTICK) {
            if (!series.isEmpty() || !pieSeries.isEmpty() || !radarSeries.isEmpty() || !funnelSeries.isEmpty()
                    || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                    || !boxplotSeries.isEmpty()
                    || !heatmapSeries.isEmpty() || radar != null
                    || xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
                throw new IllegalArgumentException("Candlestick charts must only define 2D axes and typed candlestick series");
            }
            if (xAxes.isEmpty() || yAxes.isEmpty() || candlestickSeries.isEmpty()) {
                throw new IllegalArgumentException("Candlestick charts require xAxis, yAxis, and at least one typed candlestick series");
            }
            return;
        }
        if (xAxis3D != null || yAxis3D != null || zAxis3D != null || grid3D != null || !bar3DSeries.isEmpty()) {
            throw new IllegalArgumentException("Two-dimensional chart types must not define 3D axes, grid, or 3D series");
        }
        if (radar != null || !pieSeries.isEmpty() || !radarSeries.isEmpty()
                || !funnelSeries.isEmpty() || !treeSeries.isEmpty() || !treemapSeries.isEmpty() || !sunburstSeries.isEmpty()
                || !boxplotSeries.isEmpty() || !heatmapSeries.isEmpty() || !candlestickSeries.isEmpty()) {
            throw new IllegalArgumentException("Cartesian chart types must not define specialized non-cartesian series families");
        }
    }

    /**
     * Fluent builder for the top-level chart definition.
     */
    public static final class Builder {
        private final ChartType chartType;
        private int width = 720;
        private int height = 320;
        private String backgroundColor;
        private ChartTheme theme;
        private TitleSpec title;
        private LegendSpec legend;
        private TooltipSpec tooltip;
        private GridSpec grid;
        private RadarSpec radar;
        private Axis3DSpec xAxis3D;
        private Axis3DSpec yAxis3D;
        private Axis3DSpec zAxis3D;
        private Grid3DSpec grid3D;
        private List<AxisSpec> xAxes = Collections.emptyList();
        private List<AxisSpec> yAxes = Collections.emptyList();
        private List<SeriesSpec> series = Collections.emptyList();
        private List<PieSeriesSpec> pieSeries = Collections.emptyList();
        private List<RadarSeriesSpec> radarSeries = Collections.emptyList();
        private List<FunnelSeriesSpec> funnelSeries = Collections.emptyList();
        private List<TreeSeriesSpec> treeSeries = Collections.emptyList();
        private List<TreemapSeriesSpec> treemapSeries = Collections.emptyList();
        private List<SunburstSeriesSpec> sunburstSeries = Collections.emptyList();
        private List<BoxplotSeriesSpec> boxplotSeries = Collections.emptyList();
        private List<HeatmapSeriesSpec> heatmapSeries = Collections.emptyList();
        private List<CandlestickSeriesSpec> candlestickSeries = Collections.emptyList();
        private List<Bar3DSeriesSpec> bar3DSeries = Collections.emptyList();
        private final List<OptionModule> modules = new ArrayList<OptionModule>();
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        private Builder(ChartType chartType) {
            this.chartType = Objects.requireNonNull(chartType, "chartType");
        }

        public Builder width(int width) {
            this.width = ValidationSupport.requirePositive(width, "width");
            return this;
        }

        public Builder height(int height) {
            this.height = ValidationSupport.requirePositive(height, "height");
            return this;
        }

        public Builder backgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder theme(ChartTheme theme) {
            this.theme = theme;
            return this;
        }

        public Builder title(TitleSpec title) {
            this.title = title;
            return this;
        }

        public Builder legend(LegendSpec legend) {
            this.legend = legend;
            return this;
        }

        public Builder tooltip(TooltipSpec tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder grid(GridSpec grid) {
            this.grid = grid;
            return this;
        }

        public Builder radar(RadarSpec radar) {
            this.radar = radar;
            return this;
        }

        public Builder xAxis3D(Axis3DSpec xAxis3D) {
            this.xAxis3D = xAxis3D;
            return this;
        }

        public Builder yAxis3D(Axis3DSpec yAxis3D) {
            this.yAxis3D = yAxis3D;
            return this;
        }

        public Builder zAxis3D(Axis3DSpec zAxis3D) {
            this.zAxis3D = zAxis3D;
            return this;
        }

        public Builder grid3D(Grid3DSpec grid3D) {
            this.grid3D = grid3D;
            return this;
        }

        public Builder xAxes(List<AxisSpec> xAxes) {
            this.xAxes = ValidationSupport.mutableListCopy(xAxes, "xAxes");
            return this;
        }

        public Builder yAxes(List<AxisSpec> yAxes) {
            this.yAxes = ValidationSupport.mutableListCopy(yAxes, "yAxes");
            return this;
        }

        public Builder series(List<SeriesSpec> series) {
            this.series = ValidationSupport.mutableListCopy(series, "series");
            return this;
        }

        public Builder pieSeries(List<PieSeriesSpec> pieSeries) {
            this.pieSeries = ValidationSupport.mutableListCopy(pieSeries, "pieSeries");
            return this;
        }

        public Builder radarSeries(List<RadarSeriesSpec> radarSeries) {
            this.radarSeries = ValidationSupport.mutableListCopy(radarSeries, "radarSeries");
            return this;
        }

        public Builder funnelSeries(List<FunnelSeriesSpec> funnelSeries) {
            this.funnelSeries = ValidationSupport.mutableListCopy(funnelSeries, "funnelSeries");
            return this;
        }

        public Builder treeSeries(List<TreeSeriesSpec> treeSeries) {
            this.treeSeries = ValidationSupport.mutableListCopy(treeSeries, "treeSeries");
            return this;
        }

        public Builder treemapSeries(List<TreemapSeriesSpec> treemapSeries) {
            this.treemapSeries = ValidationSupport.mutableListCopy(treemapSeries, "treemapSeries");
            return this;
        }

        public Builder sunburstSeries(List<SunburstSeriesSpec> sunburstSeries) {
            this.sunburstSeries = ValidationSupport.mutableListCopy(sunburstSeries, "sunburstSeries");
            return this;
        }

        public Builder boxplotSeries(List<BoxplotSeriesSpec> boxplotSeries) {
            this.boxplotSeries = ValidationSupport.mutableListCopy(boxplotSeries, "boxplotSeries");
            return this;
        }

        public Builder heatmapSeries(List<HeatmapSeriesSpec> heatmapSeries) {
            this.heatmapSeries = ValidationSupport.mutableListCopy(heatmapSeries, "heatmapSeries");
            return this;
        }

        public Builder candlestickSeries(List<CandlestickSeriesSpec> candlestickSeries) {
            this.candlestickSeries = ValidationSupport.mutableListCopy(candlestickSeries, "candlestickSeries");
            return this;
        }

        public Builder bar3DSeries(List<Bar3DSeriesSpec> bar3DSeries) {
            this.bar3DSeries = ValidationSupport.mutableListCopy(bar3DSeries, "bar3DSeries");
            return this;
        }

        /**
         * Registers a reusable option module such as zoom, annotation, or visual mapping.
         */
        public Builder module(OptionModule module) {
            this.modules.add(Objects.requireNonNull(module, "module"));
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public ChartSpec build() {
            return new ChartSpec(chartType, width, height, backgroundColor, theme, title, legend, tooltip, grid, radar,
                    xAxis3D, yAxis3D, zAxis3D, grid3D, xAxes, yAxes, series,
                    pieSeries, radarSeries, funnelSeries, treeSeries, treemapSeries, sunburstSeries, boxplotSeries,
                    heatmapSeries, candlestickSeries, bar3DSeries, modules, extensions);
        }
    }
}
