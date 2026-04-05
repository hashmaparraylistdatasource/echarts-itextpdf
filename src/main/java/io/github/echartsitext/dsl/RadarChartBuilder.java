package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.FunnelSeriesSpec;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.RadarIndicatorSpec;
import io.github.echartsitext.spec.RadarSeriesSpec;
import io.github.echartsitext.spec.RadarSpec;
import io.github.echartsitext.spec.RadarValueSpec;
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
 * Fluent builder for typed radar charts.
 */
public final class RadarChartBuilder {
    private int width = 720;
    private int height = 420;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(true);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final RadarSpec.Builder radar = RadarSpec.builder();
    private final RadarSeriesSpec.Builder radarSeries = RadarSeriesSpec.builder();
    private final List<RadarIndicatorSpec.Builder> indicators = new ArrayList<RadarIndicatorSpec.Builder>();
    private final List<RadarValueSpec.Builder> values = new ArrayList<RadarValueSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    RadarChartBuilder() {
    }

    public RadarChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public RadarChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public RadarChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public RadarChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public RadarChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public RadarChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public RadarChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public RadarChartBuilder radar(Consumer<RadarSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.radar);
        return this;
    }

    public RadarChartBuilder series(Consumer<RadarSeriesSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.radarSeries);
        return this;
    }

    public RadarChartBuilder indicator(String name, Number max) {
        return indicator(name, null, max, customizer -> {
        });
    }

    public RadarChartBuilder indicator(String name, Number min, Number max) {
        return indicator(name, min, max, customizer -> {
        });
    }

    public RadarChartBuilder indicator(String name, Number min, Number max, Consumer<RadarIndicatorSpec.Builder> customizer) {
        RadarIndicatorSpec.Builder indicator = RadarIndicatorSpec.builder(name).min(min).max(max);
        Objects.requireNonNull(customizer, "customizer").accept(indicator);
        this.indicators.add(indicator);
        return this;
    }

    public RadarChartBuilder value(String name, List<? extends Number> values) {
        return value(name, values, customizer -> {
        });
    }

    public RadarChartBuilder value(String name, List<? extends Number> values, Consumer<RadarValueSpec.Builder> customizer) {
        RadarValueSpec.Builder value = RadarValueSpec.builder(name, values);
        Objects.requireNonNull(customizer, "customizer").accept(value);
        this.values.add(value);
        return this;
    }

    public RadarChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public RadarChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<RadarIndicatorSpec> indicatorSpecs = new ArrayList<RadarIndicatorSpec>(indicators.size());
        for (RadarIndicatorSpec.Builder indicator : indicators) {
            indicatorSpecs.add(indicator.build());
        }
        List<RadarValueSpec> valueSpecs = new ArrayList<RadarValueSpec>(values.size());
        for (RadarValueSpec.Builder value : values) {
            valueSpecs.add(value.build());
        }

        ChartSpec.Builder builder = ChartSpec.builder(ChartType.RADAR)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .series(Collections.<SeriesSpec>emptyList())
                .pieSeries(Collections.<PieSeriesSpec>emptyList())
                .radar(radar.indicators(indicatorSpecs).build())
                .radarSeries(Collections.singletonList(radarSeries.data(valueSpecs).build()))
                .funnelSeries(Collections.<FunnelSeriesSpec>emptyList())
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
