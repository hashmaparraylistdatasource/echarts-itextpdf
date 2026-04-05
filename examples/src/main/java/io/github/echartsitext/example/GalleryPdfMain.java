package io.github.echartsitext.example;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.example.support.LocalNodeRenderService;
import io.github.echartsitext.layout.ChartLayouts;
import io.github.echartsitext.module.Annotations;
import io.github.echartsitext.module.DataZoomModule;
import io.github.echartsitext.module.VisualMaps;
import io.github.echartsitext.pdf.EchartsFigure;
import io.github.echartsitext.render.ChartRenderer;
import io.github.echartsitext.render.HttpChartRenderer;
import io.github.echartsitext.spec.CandlestickValue;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartPoint3D;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.HeatmapPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Produces a multi-page gallery PDF that mixes 2D SVG charts and a 3D PNG chart using EchartsFigure elements.
 */
public final class GalleryPdfMain {
    private GalleryPdfMain() {
    }

    public static void main(String[] args) throws IOException {
        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path outputPath = args != null && args.length > 0
                ? projectRoot.resolve(args[0]).normalize()
                : projectRoot.resolve(Paths.get("examples", "output", "chart-gallery.pdf"));

        try (LocalNodeRenderService renderService = new LocalNodeRenderService(projectRoot)) {
            renderService.start();
            renderService.requireCapabilities("svg", "png", "echarts-gl");

            ChartRenderer renderer = new HttpChartRenderer(renderService.getRenderEndpoint());
            List<EchartsFigure> figures = Arrays.asList(
                    EchartsFigure.of(buildTrendChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 1. A 2D line chart with zoom and a threshold annotation.")
                            .build(),
                    EchartsFigure.of(buildPieChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 2. A typed donut chart with named slices and automatic legend data.")
                            .build(),
                    EchartsFigure.of(buildScatterChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 3. A 2D scatter chart using visualMap for value-based coloring.")
                            .build(),
                    EchartsFigure.of(buildHeatmapChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 4. A typed heatmap chart with category axes and visual mapping.")
                            .build(),
                    EchartsFigure.of(buildCandlestickChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 5. A typed candlestick chart with category labels and OHLC data.")
                            .build(),
                    EchartsFigure.of(buildThreeDimensionalChart())
                            .renderer(renderer)
                            .autoFormat()
                            .fitWidth()
                            .caption("Figure 6. A 3D bar chart rendered as PNG and embedded as an iText figure.")
                            .build()
            );

            Files.createDirectories(outputPath.getParent());
            try (PdfWriter writer = new PdfWriter(Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);
                document.add(new Paragraph("echarts-itextpdf Gallery")
                        .setFontSize(16f)
                        .setMarginBottom(16f));
                for (int i = 0; i < figures.size(); i++) {
                    if (i > 0) {
                        document.add(new AreaBreak());
                    }
                    document.add(figures.get(i));
                }
                document.close();
            }

            System.out.println("Gallery PDF generated at: " + outputPath);
            System.out.println("Render service log: " + renderService.getLogFile());
        }
    }

    private static ChartSpec buildTrendChart() {
        return Charts.line()
                .size(960, 420)
                .layout(ChartLayouts.report())
                .title("Line Chart")
                .xAxis(axis -> axis.name("Time (min)").range(0d, 60d).splitNumber(6))
                .yAxis(axis -> axis.name("mAU").autoRange().splitNumber(8))
                .series("Batch A", Arrays.asList(
                        new ChartPoint(0d, 1.0d),
                        new ChartPoint(10d, 4.5d),
                        new ChartPoint(20d, 9.2d),
                        new ChartPoint(30d, 7.1d),
                        new ChartPoint(40d, 12.4d),
                        new ChartPoint(50d, 8.8d),
                        new ChartPoint(60d, 10.1d)
                ), series -> series.smooth(Boolean.TRUE).lineWidth(1.8d))
                .series("Batch B", Arrays.asList(
                        new ChartPoint(0d, 0.6d),
                        new ChartPoint(10d, 2.1d),
                        new ChartPoint(20d, 5.3d),
                        new ChartPoint(30d, 6.8d),
                        new ChartPoint(40d, 7.5d),
                        new ChartPoint(50d, 9.2d),
                        new ChartPoint(60d, 9.8d)
                ), series -> series.color("#bb1005").smooth(Boolean.TRUE).lineWidth(1.4d))
                .module(DataZoomModule.inside().start(0).end(100).build())
                .module(Annotations.horizontalLine("Target Range", 8.5d))
                .build();
    }

    private static ChartSpec buildScatterChart() {
        return Charts.scatter()
                .size(960, 420)
                .layout(ChartLayouts.compact())
                .title("Scatter Chart")
                .xAxis(axis -> axis.name("Injection").range(0d, 12d).splitNumber(6))
                .yAxis(axis -> axis.name("Response").range(0d, 15d).splitNumber(5))
                .series("Samples", Arrays.asList(
                        new ChartPoint(1d, 2.1d),
                        new ChartPoint(2d, 3.8d),
                        new ChartPoint(3d, 4.2d),
                        new ChartPoint(4d, 6.4d),
                        new ChartPoint(5d, 7.9d),
                        new ChartPoint(6d, 9.8d),
                        new ChartPoint(7d, 10.2d),
                        new ChartPoint(8d, 11.7d),
                        new ChartPoint(9d, 13.4d),
                        new ChartPoint(10d, 12.2d)
                ), series -> series.symbol("circle").symbolSize(12))
                .module(VisualMaps.continuous(0d, 15d)
                        .dimension(1)
                        .seriesIndex(0)
                        .calculable(Boolean.TRUE)
                        .left("right")
                        .top("middle")
                        .inRange("color", Arrays.asList("#006e54", "#f1c40f", "#bb1005"))
                        .build())
                .build();
    }

    private static ChartSpec buildPieChart() {
        return Charts.pie()
                .size(720, 420)
                .title("Pie Chart")
                .donut("35%", "68%")
                .slice("Automated", 48)
                .slice("Scheduled", 27)
                .slice("Manual", 15)
                .slice("Exception", 10, slice -> slice.selected(Boolean.TRUE))
                .build();
    }

    private static ChartSpec buildHeatmapChart() {
        return Charts.heatmap()
                .size(960, 420)
                .layout(ChartLayouts.compact())
                .title("Heatmap Chart")
                .xAxis(axis -> axis.name("Day"))
                .yAxis(axis -> axis.name("Shift"))
                .xCategories(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri"))
                .yCategories(Arrays.asList("Morning", "Noon", "Evening"))
                .series("Load", Arrays.asList(
                        new HeatmapPoint(0, 0, 12),
                        new HeatmapPoint(1, 0, 18),
                        new HeatmapPoint(2, 0, 24),
                        new HeatmapPoint(3, 0, 17),
                        new HeatmapPoint(4, 0, 14),
                        new HeatmapPoint(0, 1, 15),
                        new HeatmapPoint(1, 1, 20),
                        new HeatmapPoint(2, 1, 28),
                        new HeatmapPoint(3, 1, 23),
                        new HeatmapPoint(4, 1, 19),
                        new HeatmapPoint(0, 2, 9),
                        new HeatmapPoint(1, 2, 13),
                        new HeatmapPoint(2, 2, 16),
                        new HeatmapPoint(3, 2, 11),
                        new HeatmapPoint(4, 2, 8)
                ))
                .module(VisualMaps.continuous(0d, 30d)
                        .dimension(2)
                        .seriesIndex(0)
                        .calculable(Boolean.TRUE)
                        .left("right")
                        .top("middle")
                        .text("High", "Low")
                        .inRange("color", Arrays.asList("#d8f3dc", "#52b788", "#081c15"))
                        .build())
                .build();
    }

    private static ChartSpec buildCandlestickChart() {
        return Charts.candlestick()
                .size(960, 420)
                .layout(ChartLayouts.report())
                .title("Candlestick Chart")
                .xAxis(axis -> axis.name("Date"))
                .yAxis(axis -> axis.name("Price"))
                .categories(Arrays.asList("04-01", "04-02", "04-03", "04-04", "04-05", "04-06"))
                .series("Price", Arrays.asList(
                        new CandlestickValue(20, 24, 18, 26),
                        new CandlestickValue(24, 22, 21, 25),
                        new CandlestickValue(22, 28, 20, 30),
                        new CandlestickValue(28, 26, 24, 31),
                        new CandlestickValue(26, 29, 25, 33),
                        new CandlestickValue(29, 27, 26, 32)
                ))
                .module(DataZoomModule.inside().start(0).end(100).build())
                .build();
    }

    private static ChartSpec buildThreeDimensionalChart() {
        List<String> temperature = Arrays.asList("50C", "60C", "70C", "80C");
        List<String> flowRate = Arrays.asList("0.8", "1.0", "1.2", "1.4");
        List<ChartPoint3D> values = new ArrayList<ChartPoint3D>();
        values.add(point3d(0, 0, 4.1));
        values.add(point3d(0, 1, 5.0));
        values.add(point3d(0, 2, 6.4));
        values.add(point3d(0, 3, 7.1));
        values.add(point3d(1, 0, 5.6));
        values.add(point3d(1, 1, 7.3));
        values.add(point3d(1, 2, 8.5));
        values.add(point3d(1, 3, 9.4));
        values.add(point3d(2, 0, 6.2));
        values.add(point3d(2, 1, 8.7));
        values.add(point3d(2, 2, 9.9));
        values.add(point3d(2, 3, 10.8));
        values.add(point3d(3, 0, 7.5));
        values.add(point3d(3, 1, 9.1));
        values.add(point3d(3, 2, 10.6));
        values.add(point3d(3, 3, 12.4));

        return Charts.bar3D()
                .size(960, 540)
                .title("3D Bar Chart")
                .xAxis3D(axis -> axis.name("Temperature").categories(temperature))
                .yAxis3D(axis -> axis.name("Flow Rate").categories(flowRate))
                .zAxis3D(axis -> axis.name("Yield"))
                .grid3D(grid -> grid
                        .boxSize(110, 90)
                        .viewAngles(22, 32)
                        .mainLight(1.1d, Boolean.TRUE)
                        .ambientLight(0.45d))
                .series("Yield Surface", values, series -> series
                        .shading("lambert")
                        .bevelSize(0.15d)
                        .labelShow(Boolean.FALSE)
                        .emphasisLabel(Boolean.TRUE, 12))
                .module(VisualMaps.continuous(0d, 13d)
                        .dimension(2)
                        .seriesIndex(0)
                        .calculable(Boolean.TRUE)
                        .left("right")
                        .top("middle")
                        .text("High", "Low")
                        .inRange("color", Arrays.asList("#006e54", "#f1c40f", "#bb1005"))
                        .build())
                .build();
    }

    private static ChartPoint3D point3d(double x, double y, double z) {
        return new ChartPoint3D(Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
    }
}
