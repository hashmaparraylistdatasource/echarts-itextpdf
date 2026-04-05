package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.PieSliceSpec;
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
 * Fluent builder for first-class pie and donut charts.
 */
public final class PieChartBuilder {
    private int width = 720;
    private int height = 420;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(true);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final PieSeriesSpec.Builder pieSeries = PieSeriesSpec.builder();
    private final List<PieSliceSpec.Builder> slices = new ArrayList<PieSliceSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    PieChartBuilder() {
    }

    public PieChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public PieChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public PieChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public PieChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public PieChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public PieChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public PieChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public PieChartBuilder series(Consumer<PieSeriesSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.pieSeries);
        return this;
    }

    public PieChartBuilder radius(String radius) {
        this.pieSeries.radius(radius);
        return this;
    }

    public PieChartBuilder donut(String innerRadius, String outerRadius) {
        this.pieSeries.donut(innerRadius, outerRadius);
        return this;
    }

    public PieChartBuilder center(String x, String y) {
        this.pieSeries.center(x, y);
        return this;
    }

    public PieChartBuilder slice(String name, Number value) {
        return slice(name, value, customizer -> {
        });
    }

    public PieChartBuilder slice(String name, Number value, Consumer<PieSliceSpec.Builder> customizer) {
        PieSliceSpec.Builder slice = PieSliceSpec.builder(name, value);
        Objects.requireNonNull(customizer, "customizer").accept(slice);
        this.slices.add(slice);
        return this;
    }

    public PieChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public PieChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<PieSliceSpec> sliceSpecs = new ArrayList<PieSliceSpec>(slices.size());
        for (PieSliceSpec.Builder slice : slices) {
            sliceSpecs.add(slice.build());
        }
        ChartSpec.Builder builder = ChartSpec.builder(ChartType.PIE)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .xAxes(Collections.<AxisSpec>emptyList())
                .yAxes(Collections.<AxisSpec>emptyList())
                .series(Collections.<SeriesSpec>emptyList())
                .pieSeries(Collections.singletonList(pieSeries.data(sliceSpecs).build()))
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
