package io.github.echartsitext.spec;

import io.github.echartsitext.module.OptionModule;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecValidationTest {
    @Test
    void shouldRejectNonPositiveChartDimensions() {
        IllegalArgumentException widthException = assertThrows(IllegalArgumentException.class,
                () -> ChartSpec.builder(ChartType.LINE).width(0));
        IllegalArgumentException heightException = assertThrows(IllegalArgumentException.class,
                () -> ChartSpec.builder(ChartType.LINE).height(-1));

        assertTrue(widthException.getMessage().contains("width"));
        assertTrue(heightException.getMessage().contains("height"));
    }

    @Test
    void shouldRejectNullChartCollectionsAndModules() {
        OptionModule module = (optionTarget, moduleContext) -> {
        };

        assertThrows(NullPointerException.class, () -> ChartSpec.builder(ChartType.LINE).xAxes(null));
        assertThrows(NullPointerException.class, () -> ChartSpec.builder(ChartType.LINE).series(Arrays.asList(
                SeriesSpec.builder("line", "A", Collections.singletonList(new ChartPoint(0d, 1d))).build(),
                null
        )));
        assertThrows(NullPointerException.class, () -> ChartSpec.builder(ChartType.LINE).module(null));
        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.LINE).raw(" ", Boolean.TRUE));

        ChartSpec spec = ChartSpec.builder(ChartType.LINE)
                .module(module)
                .series(Collections.singletonList(
                        SeriesSpec.builder("line", "A", Collections.singletonList(new ChartPoint(0d, 1d))).build()
                ))
                .build();
        assertTrue(spec.getModules().contains(module));
    }

    @Test
    void shouldRejectInvalidSeriesArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> SeriesSpec.builder(" ", "A", Collections.singletonList(new ChartPoint(0d, 1d))));
        assertThrows(NullPointerException.class,
                () -> SeriesSpec.builder("line", "A", Arrays.asList(new ChartPoint(0d, 1d), null)));
        assertThrows(IllegalArgumentException.class,
                () -> SeriesSpec.builder("line", "A", Collections.singletonList(new ChartPoint(0d, 1d)))
                        .xAxisIndex(-1));
        assertThrows(IllegalArgumentException.class,
                () -> SeriesSpec.builder("line", "A", Collections.singletonList(new ChartPoint(0d, 1d)))
                        .lineWidth(-0.1d));
    }

    @Test
    void shouldRejectInvalidAxisAndTextStyleArguments() {
        assertThrows(IllegalArgumentException.class, () -> AxisSpec.builder().interval(0d));
        assertThrows(IllegalArgumentException.class, () -> AxisSpec.builder().splitNumber(0));
        assertThrows(IllegalArgumentException.class, () -> AxisSpec.builder().nameGap(-1));
        assertThrows(IllegalArgumentException.class, () -> TextStyleSpec.builder().fontSize(0));
    }

    @Test
    void shouldKeepSpecLongConstructorsOffThePublicApiSurface() throws Exception {
        assertTrue(!Modifier.isPublic(singleConstructor(ChartSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(AxisSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(SeriesSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(GridSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(LegendSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(TooltipSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(TitleSpec.class).getModifiers()));
        assertTrue(!Modifier.isPublic(singleConstructor(TextStyleSpec.class).getModifiers()));
    }

    private static Constructor<?> singleConstructor(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new AssertionError("Expected exactly one constructor on " + type.getName());
        }
        return constructors[0];
    }

    @Test
    void shouldRejectMixingTwoDimensionalAndThreeDimensionalModelShapes() {
        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.LINE)
                .xAxis3D(Axis3DSpec.category().name("X").categories(Arrays.asList("A", "B")).build())
                .bar3DSeries(Collections.singletonList(
                        Bar3DSeriesSpec.builder()
                                .name("ThreeD")
                                .data(Collections.singletonList(new ChartPoint3D(0, 0, 1)))
                                .build()
                ))
                .build());

        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.BAR_3D)
                .xAxes(Collections.singletonList(AxisSpec.builder().name("X").build()))
                .yAxes(Collections.singletonList(AxisSpec.builder().name("Y").build()))
                .series(Collections.singletonList(
                        SeriesSpec.builder("bar", "A", Collections.singletonList(new ChartPoint(0d, 1d))).build()
                ))
                .xAxis3D(Axis3DSpec.category().name("X").categories(Arrays.asList("A", "B")).build())
                .yAxis3D(Axis3DSpec.category().name("Y").categories(Arrays.asList("C", "D")).build())
                .zAxis3D(Axis3DSpec.value().name("Z").build())
                .bar3DSeries(Collections.singletonList(
                        Bar3DSeriesSpec.builder()
                                .name("ThreeD")
                                .data(Collections.singletonList(new ChartPoint3D(0, 0, 1)))
                                .build()
                ))
                .build());
    }

    @Test
    void shouldRejectMixingTypedSpecializedSeriesFamilies() {
        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.PIE)
                .xAxes(Collections.singletonList(AxisSpec.builder().name("X").build()))
                .pieSeries(Collections.singletonList(
                        PieSeriesSpec.builder()
                                .data(Collections.singletonList(PieSliceSpec.builder("A", 10).build()))
                                .build()
                ))
                .build());

        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.HEATMAP)
                .xAxes(Collections.singletonList(AxisSpec.builder().type("category").categories(Arrays.asList("A")).build()))
                .yAxes(Collections.singletonList(AxisSpec.builder().type("category").categories(Arrays.asList("B")).build()))
                .candlestickSeries(Collections.singletonList(
                        CandlestickSeriesSpec.builder()
                                .data(Collections.singletonList(new CandlestickValue(1, 2, 0, 3)))
                                .build()
                ))
                .build());

        assertThrows(IllegalArgumentException.class, () -> ChartSpec.builder(ChartType.CANDLESTICK)
                .xAxes(Collections.singletonList(AxisSpec.builder().type("category").categories(Arrays.asList("A")).build()))
                .yAxes(Collections.singletonList(AxisSpec.builder().type("value").build()))
                .heatmapSeries(Collections.singletonList(
                        HeatmapSeriesSpec.builder()
                                .data(Collections.singletonList(new HeatmapPoint(0, 0, 1)))
                                .build()
                ))
                .build());
    }
}
