package io.github.echartsitext.dsl;

import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.layout.ChartLayoutProfile;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.AxisTitleLayoutMode;
import io.github.echartsitext.spec.Bar3DSeriesSpec;
import io.github.echartsitext.spec.CandlestickSeriesSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.ChartType;
import io.github.echartsitext.spec.GridSpec;
import io.github.echartsitext.spec.HeatmapPoint;
import io.github.echartsitext.spec.HeatmapSeriesSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
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
 * Fluent builder for typed heatmap charts.
 */
public final class HeatmapChartBuilder {
    private int width = 840;
    private int height = 420;
    private String backgroundColor;
    private ChartTheme theme = ChartTheme.report();
    private final TitleSpec.Builder title = TitleSpec.builder();
    private final LegendSpec.Builder legend = LegendSpec.builder().show(false);
    private final TooltipSpec.Builder tooltip = TooltipSpec.builder().trigger("item");
    private final GridSpec.Builder grid = GridSpec.builder();
    private final AxisSpec.Builder xAxis = AxisSpec.builder().type("category");
    private final AxisSpec.Builder yAxis = AxisSpec.builder().type("category");
    private final HeatmapSeriesSpec.Builder series = HeatmapSeriesSpec.builder();
    private final List<OptionModule> modules = new ArrayList<OptionModule>();
    private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

    HeatmapChartBuilder() {
    }

    public HeatmapChartBuilder size(int width, int height) {
        this.width = ValidationSupport.requirePositive(width, "width");
        this.height = ValidationSupport.requirePositive(height, "height");
        return this;
    }

    public HeatmapChartBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public HeatmapChartBuilder theme(ChartTheme theme) {
        this.theme = Objects.requireNonNull(theme, "theme");
        return this;
    }

    public HeatmapChartBuilder title(String text) {
        this.title.text(text).show(text != null && text.trim().length() > 0);
        return this;
    }

    public HeatmapChartBuilder title(Consumer<TitleSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.title);
        return this;
    }

    public HeatmapChartBuilder legend(Consumer<LegendSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.legend);
        return this;
    }

    public HeatmapChartBuilder tooltip(Consumer<TooltipSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.tooltip);
        return this;
    }

    public HeatmapChartBuilder layout(ChartLayoutProfile profile) {
        ChartLayoutProfile resolved = Objects.requireNonNull(profile, "profile");
        applyLegendLayout(resolved);
        applyGridLayout(resolved);
        applyAxisLayout(resolved.getXAxisTitleLayoutMode(), this.xAxis);
        applyAxisLayout(resolved.getYAxisTitleLayoutMode(), this.yAxis);
        return this;
    }

    public HeatmapChartBuilder grid(Consumer<GridSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.grid);
        return this;
    }

    public HeatmapChartBuilder xAxis(Consumer<AxisSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.xAxis);
        return this;
    }

    public HeatmapChartBuilder yAxis(Consumer<AxisSpec.Builder> customizer) {
        Objects.requireNonNull(customizer, "customizer").accept(this.yAxis);
        return this;
    }

    public HeatmapChartBuilder xCategories(List<?> categories) {
        this.xAxis.categories(categories);
        return this;
    }

    public HeatmapChartBuilder yCategories(List<?> categories) {
        this.yAxis.categories(categories);
        return this;
    }

    public HeatmapChartBuilder series(String name, List<HeatmapPoint> data) {
        return series(name, data, customizer -> {
        });
    }

    public HeatmapChartBuilder series(String name, List<HeatmapPoint> data, Consumer<HeatmapSeriesSpec.Builder> customizer) {
        this.series.name(name).data(ValidationSupport.mutableListCopy(data, "data"));
        Objects.requireNonNull(customizer, "customizer").accept(this.series);
        return this;
    }

    public HeatmapChartBuilder module(OptionModule module) {
        this.modules.add(Objects.requireNonNull(module, "module"));
        return this;
    }

    public HeatmapChartBuilder raw(String key, Object value) {
        this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
        return this;
    }

    public ChartSpec build() {
        ChartSpec.Builder builder = ChartSpec.builder(ChartType.HEATMAP)
                .width(width)
                .height(height)
                .backgroundColor(backgroundColor)
                .theme(theme)
                .title(title.build())
                .legend(legend.build())
                .tooltip(tooltip.build())
                .grid(grid.build())
                .xAxes(Collections.singletonList(xAxis.build()))
                .yAxes(Collections.singletonList(yAxis.build()))
                .series(Collections.<SeriesSpec>emptyList())
                .pieSeries(Collections.<PieSeriesSpec>emptyList())
                .heatmapSeries(Collections.singletonList(series.build()))
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

    private void applyLegendLayout(ChartLayoutProfile profile) {
        this.legend(customizer -> {
            if (profile.getLegendShow() != null) {
                customizer.show(profile.getLegendShow().booleanValue());
            }
            if (profile.getLegendOrient() != null) {
                customizer.orient(profile.getLegendOrient());
            }
            if (profile.getLegendLeft() != null) {
                customizer.left(profile.getLegendLeft());
            }
            if (profile.getLegendTop() != null) {
                customizer.top(profile.getLegendTop());
            }
        });
    }

    private void applyGridLayout(ChartLayoutProfile profile) {
        this.grid(customizer -> {
            if (profile.getGridLeft() != null) {
                customizer.left(profile.getGridLeft());
            }
            if (profile.getGridRight() != null) {
                customizer.right(profile.getGridRight());
            }
            if (profile.getGridTop() != null) {
                customizer.top(profile.getGridTop());
            }
            if (profile.getGridBottom() != null) {
                customizer.bottom(profile.getGridBottom());
            }
            if (profile.getContainLabel() != null) {
                customizer.containLabel(profile.getContainLabel());
            }
        });
    }

    private void applyAxisLayout(AxisTitleLayoutMode mode, AxisSpec.Builder axis) {
        if (mode != null) {
            axis.titleLayoutMode(mode);
        }
    }
}
