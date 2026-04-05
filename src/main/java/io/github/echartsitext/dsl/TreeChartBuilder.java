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
import io.github.echartsitext.spec.TreeSeriesSpec;
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
 * Fluent builder for typed tree charts.
 */
public final class TreeChartBuilder {
    private int width = 860;
    private int height = 480;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(false);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final TreeSeriesSpec.Builder treeSeries = TreeSeriesSpec.builder();
    private final List<HierarchyNodeSpec.Builder> roots = new ArrayList<HierarchyNodeSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    TreeChartBuilder() {
    }

    public TreeChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public TreeChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TreeChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public TreeChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public TreeChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public TreeChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public TreeChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public TreeChartBuilder series(Consumer<TreeSeriesSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.treeSeries);
        return this;
    }

    public TreeChartBuilder node(String name, Number value) {
        return node(name, value, customizer -> {
        });
    }

    public TreeChartBuilder node(String name, Number value, Consumer<HierarchyNodeSpec.Builder> customizer) {
        HierarchyNodeSpec.Builder node = HierarchyNodeSpec.builder(name).value(value);
        Objects.requireNonNull(customizer, "customizer").accept(node);
        this.roots.add(node);
        return this;
    }

    public TreeChartBuilder branch(String name, Consumer<HierarchyNodeSpec.Builder> customizer) {
        HierarchyNodeSpec.Builder node = HierarchyNodeSpec.builder(name);
        Objects.requireNonNull(customizer, "customizer").accept(node);
        this.roots.add(node);
        return this;
    }

    public TreeChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public TreeChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<HierarchyNodeSpec> data = new ArrayList<HierarchyNodeSpec>(roots.size());
        for (HierarchyNodeSpec.Builder root : roots) {
            data.add(root.build());
        }
        ChartSpec.Builder builder = ChartSpec.builder(ChartType.TREE)
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
                .treeSeries(Collections.singletonList(treeSeries.data(data).build()))
                .treemapSeries(Collections.<TreemapSeriesSpec>emptyList())
                .sunburstSeries(Collections.<SunburstSeriesSpec>emptyList())
                .boxplotSeries(Collections.<BoxplotSeriesSpec>emptyList())
                .heatmapSeries(Collections.<HeatmapSeriesSpec>emptyList())
                .candlestickSeries(Collections.<CandlestickSeriesSpec>emptyList())
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
