package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.layout.ChartLayoutPreset;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.GridSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.SeriesSpec;
import io.github.echartsitext.spec.TitleSpec;
import io.github.echartsitext.spec.TooltipSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Fluent builder for common Cartesian charts such as line, bar, and scatter.
 */
public final class CartesianChartBuilder {
    private final ChartType chartType;
    private int width = 720;
    private int height = 320;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder();
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().axisPointerType("line");
    private final GridSpec.Builder grid = GridSpec.builder();
    private final List<Consumer<AxisSpec.Builder>> xAxisDefaults = new ArrayList<Consumer<AxisSpec.Builder>>();
    private final List<Consumer<AxisSpec.Builder>> yAxisDefaults = new ArrayList<Consumer<AxisSpec.Builder>>();
    private final List<AxisSpec.Builder> xAxes = new ArrayList<>();
    private final List<AxisSpec.Builder> yAxes = new ArrayList<>();
    private final List<SeriesSpec.Builder> series = new ArrayList<>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<>();

    CartesianChartBuilder(ChartType chartType) {
        this.chartType = Objects.requireNonNull(chartType, "chartType");
        this.xAxes.add(createAxisBuilder(this.xAxisDefaults));
        this.yAxes.add(createAxisBuilder(this.yAxisDefaults));
    }

    public CartesianChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public CartesianChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public CartesianChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public CartesianChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public CartesianChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public CartesianChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public CartesianChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public CartesianChartBuilder layout(ChartLayoutPreset preset) {
        Objects.requireNonNull(preset, "preset").apply(this);
        return this;
    }

    public CartesianChartBuilder grid(Consumer<GridSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.grid);
        return this;
    }

    public CartesianChartBuilder xAxis(Consumer<AxisSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.xAxes.get(0));
        return this;
    }

    public CartesianChartBuilder allXAxes(Consumer<AxisSpec.Builder> customizer) {
        applyAxisDefault(this.xAxisDefaults, this.xAxes, Objects.requireNonNull(customizer, "customizer"));
        return this;
    }

    public CartesianChartBuilder addXAxis(Consumer<AxisSpec.Builder> customizer) {
        AxisSpec.Builder builder = createAxisBuilder(this.xAxisDefaults);
        Objects.requireNonNull(customizer, "customizer").accept(builder);
        this.xAxes.add(builder);
        return this;
    }

    public CartesianChartBuilder yAxis(Consumer<AxisSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.yAxes.get(0));
        return this;
    }

    public CartesianChartBuilder allYAxes(Consumer<AxisSpec.Builder> customizer) {
        applyAxisDefault(this.yAxisDefaults, this.yAxes, Objects.requireNonNull(customizer, "customizer"));
        return this;
    }

    public CartesianChartBuilder addYAxis(Consumer<AxisSpec.Builder> customizer) {
        AxisSpec.Builder builder = createAxisBuilder(this.yAxisDefaults);
        Objects.requireNonNull(customizer, "customizer").accept(builder);
        this.yAxes.add(builder);
        return this;
    }

    public CartesianChartBuilder series(String name, List<ChartPoint> data) {
        return series(name, data, spec -> {
        });
    }

    public CartesianChartBuilder series(String name, List<ChartPoint> data, Consumer<SeriesSpec.Builder> customizer) {
        SeriesSpec.Builder builder = SeriesSpec.builder(chartType.echartsType(), name,
                ValidationSupport.mutableListCopy(data, "data"));
        Objects.requireNonNull(customizer, "customizer").accept(builder);
        this.series.add(builder);
        return this;
    }

    /**
     * Adds a reusable option module such as zoom, annotations, or future visual mappings.
     */
    public CartesianChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public CartesianChartBuilder raw(String key, Object value) {
        // Raw extensions are the escape hatch for ECharts features that are not modeled yet.
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        // Builders stay mutable while assembling the chart, then collapse into immutable specs here.
        List<AxisSpec> xAxisSpecs = xAxes.stream().map(AxisSpec.Builder::build).collect(Collectors.toList());
        List<AxisSpec> yAxisSpecs = yAxes.stream().map(AxisSpec.Builder::build).collect(Collectors.toList());
        List<SeriesSpec> seriesSpecs = series.stream().map(SeriesSpec.Builder::build).collect(Collectors.toList());

        ChartSpec.Builder builder = ChartSpec.builder(chartType)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .grid(grid.build())
                .xAxes(xAxisSpecs)
                .yAxes(yAxisSpecs)
                .series(seriesSpecs);
        for (OptionModule module : modules) {
            builder.module(module);
        }
        extensions.forEach(builder::raw);
        return builder.build();
    }

    private AxisSpec.Builder createAxisBuilder(List<Consumer<AxisSpec.Builder>> defaults) {
        AxisSpec.Builder builder = AxisSpec.builder();
        for (Consumer<AxisSpec.Builder> axisDefault : defaults) {
            axisDefault.accept(builder);
        }
        return builder;
    }

    private void applyAxisDefault(List<Consumer<AxisSpec.Builder>> defaults,
                                  List<AxisSpec.Builder> axes,
                                  Consumer<AxisSpec.Builder> customizer) {
        defaults.add(customizer);
        for (AxisSpec.Builder axis : axes) {
            customizer.accept(axis);
        }
    }
}
