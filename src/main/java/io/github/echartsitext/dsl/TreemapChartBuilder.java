package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.BoxplotSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.FunnelSeriesSpec;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.HierarchyNodeSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.RadarSeriesSpec;
import io.github.echartsitext.spec.SeriesSpec;
import io.github.echartsitext.spec.SunburstSeriesSpec;
import io.github.echartsitext.spec.TitleSpec;
import io.github.echartsitext.spec.TooltipSpec;
import io.github.echartsitext.spec.TreemapSeriesSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for typed treemap charts.
 */
public final class TreemapChartBuilder {
    private int width = 760;
    private int height = 460;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(false);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final TreemapSeriesSpec.Builder treemapSeries = TreemapSeriesSpec.builder();
    private final List<HierarchyNodeSpec.Builder> nodes = new ArrayList<HierarchyNodeSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    TreemapChartBuilder() {
    }

    public TreemapChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public TreemapChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TreemapChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public TreemapChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public TreemapChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public TreemapChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public TreemapChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public TreemapChartBuilder series(Consumer<TreemapSeriesSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.treemapSeries);
        return this;
    }

    public TreemapChartBuilder node(String name, Number value) {
        return node(name, value, customizer -> {
        });
    }

    public TreemapChartBuilder node(String name, Number value, Consumer<HierarchyNodeSpec.Builder> customizer) {
        HierarchyNodeSpec.Builder node = HierarchyNodeSpec.builder(name).value(value);
        Objects.requireNonNull(customizer, "customizer").accept(node);
        this.nodes.add(node);
        return this;
    }

    public TreemapChartBuilder branch(String name, Consumer<HierarchyNodeSpec.Builder> customizer) {
        HierarchyNodeSpec.Builder node = HierarchyNodeSpec.builder(name);
        Objects.requireNonNull(customizer, "customizer").accept(node);
        this.nodes.add(node);
        return this;
    }

    public TreemapChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public TreemapChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<HierarchyNodeSpec> data = new ArrayList<HierarchyNodeSpec>(nodes.size());
        for (HierarchyNodeSpec.Builder node : nodes) {
            data.add(node.build());
        }
        ChartSpec.Builder builder = ChartSpec.builder(ChartType.TREEMAP)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .series(Collections.<SeriesSpec>emptyList())
                .pieSeries(Collections.<PieSeriesSpec>emptyList())
                .radarSeries(Collections.<RadarSeriesSpec>emptyList())
                .funnelSeries(Collections.<FunnelSeriesSpec>emptyList())
                .boxplotSeries(Collections.<BoxplotSeriesSpec>emptyList())
                .heatmapSeries(Collections.<HeatmapSeriesSpec>emptyList())
                .candlestickSeries(Collections.<CandlestickSeriesSpec>emptyList())
                .treemapSeries(Collections.singletonList(treemapSeries.data(data).build()))
                .sunburstSeries(Collections.<SunburstSeriesSpec>emptyList())
                .bar3DSeries(Collections.<Bar3DSeriesSpec>emptyList());
        for (OptionModule module : modules) {
            builder.module(module);
        }
        for (Map.Entry<String, Object> entry : extensions.entrySet()) {
            builder.raw(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
