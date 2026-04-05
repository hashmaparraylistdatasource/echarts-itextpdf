package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.FunnelSeriesSpec;
import io.github.echartsitext.spec.FunnelSliceSpec;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.RadarSeriesSpec;
import io.github.echartsitext.spec.SeriesSpec;
import io.github.echartsitext.spec.TitleSpec;
import io.github.echartsitext.spec.TooltipSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for typed funnel charts.
 */
public final class FunnelChartBuilder {
    private int width = 720;
    private int height = 420;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(true);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final FunnelSeriesSpec.Builder funnelSeries = FunnelSeriesSpec.builder();
    private final List<FunnelSliceSpec.Builder> slices = new ArrayList<FunnelSliceSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    FunnelChartBuilder() {
    }

    public FunnelChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public FunnelChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public FunnelChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public FunnelChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public FunnelChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public FunnelChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public FunnelChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public FunnelChartBuilder series(Consumer<FunnelSeriesSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.funnelSeries);
        return this;
    }

    public FunnelChartBuilder slice(String name, Number value) {
        return slice(name, value, customizer -> {
        });
    }

    public FunnelChartBuilder slice(String name, Number value, Consumer<FunnelSliceSpec.Builder> customizer) {
        FunnelSliceSpec.Builder slice = FunnelSliceSpec.builder(name, value);
        Objects.requireNonNull(customizer, "customizer").accept(slice);
        this.slices.add(slice);
        return this;
    }

    public FunnelChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public FunnelChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<FunnelSliceSpec> sliceSpecs = new ArrayList<FunnelSliceSpec>(slices.size());
        for (FunnelSliceSpec.Builder slice : slices) {
            sliceSpecs.add(slice.build());
        }

        ChartSpec.Builder builder = ChartSpec.builder(ChartType.FUNNEL)
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
                .funnelSeries(Collections.singletonList(funnelSeries.data(sliceSpecs).build()))
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
