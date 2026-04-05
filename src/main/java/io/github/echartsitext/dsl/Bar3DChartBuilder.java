package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.Axis3DSpec;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.ChartPoint3D;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.Grid3DSpec;
import io.github.echartsitext.spec.LegendSpec;
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
 * Fluent builder for typed 3D bar charts rendered through ECharts GL.
 */
public final class Bar3DChartBuilder {
    private int width = 960;
    private int height = 540;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(false);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final Axis3DSpec.Builder xAxis3D = Axis3DSpec.category();
    private final Axis3DSpec.Builder yAxis3D = Axis3DSpec.category();
    private final Axis3DSpec.Builder zAxis3D = Axis3DSpec.value();
    private final Grid3DSpec.Builder grid3D = Grid3DSpec.builder();
    private final List<Bar3DSeriesSpec.Builder> series = new ArrayList<Bar3DSeriesSpec.Builder>();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    Bar3DChartBuilder() {
    }

    public Bar3DChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public Bar3DChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Bar3DChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public Bar3DChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public Bar3DChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public Bar3DChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public Bar3DChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public Bar3DChartBuilder xAxis3D(Consumer<Axis3DSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.xAxis3D);
        return this;
    }

    public Bar3DChartBuilder yAxis3D(Consumer<Axis3DSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.yAxis3D);
        return this;
    }

    public Bar3DChartBuilder zAxis3D(Consumer<Axis3DSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.zAxis3D);
        return this;
    }

    public Bar3DChartBuilder grid3D(Consumer<Grid3DSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.grid3D);
        return this;
    }

    public Bar3DChartBuilder series(String name, List<ChartPoint3D> data) {
        return series(name, data, spec -> {
        });
    }

    public Bar3DChartBuilder series(String name, List<ChartPoint3D> data, Consumer<Bar3DSeriesSpec.Builder> customizer) {
        Bar3DSeriesSpec.Builder builder = Bar3DSeriesSpec.builder()
                .name(name)
                .data(ValidationSupport.mutableListCopy(data, "data"));
        Objects.requireNonNull(customizer, "customizer").accept(builder);
        this.series.add(builder);
        return this;
    }

    public Bar3DChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public Bar3DChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        List<Bar3DSeriesSpec> seriesOptions = new ArrayList<Bar3DSeriesSpec>(series.size());
        for (Bar3DSeriesSpec.Builder seriesBuilder : series) {
            seriesOptions.add(seriesBuilder.build());
        }
        ChartSpec.Builder builder = ChartSpec.builder(ChartType.BAR_3D)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .xAxis3D(xAxis3D.build())
                .yAxis3D(yAxis3D.build())
                .zAxis3D(zAxis3D.build())
                .grid3D(grid3D.build())
                .xAxes(Collections.emptyList())
                .yAxes(Collections.emptyList())
                .series(Collections.emptyList())
                .bar3DSeries(seriesOptions);

        for (OptionModule module : modules) {
            builder.module(module);
        }
        for (Map.Entry<String, Object> entry : extensions.entrySet()) {
            builder.raw(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
