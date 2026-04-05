package io.github.echartsitext.example;

import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.example.support.LocalNodeRenderService;
import io.github.echartsitext.layout.ChartLayouts;
import io.github.echartsitext.module.Annotations;
import io.github.echartsitext.module.DataZoomModule;
import io.github.echartsitext.pdf.EchartsFigure;
import io.github.echartsitext.pdf.PdfChartWriter;
import io.github.echartsitext.render.ChartRenderer;
import io.github.echartsitext.render.HttpChartRenderer;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * End-to-end demo that starts the local render service and writes a PDF to disk.
 */
public final class SamplePdfMain {
    private SamplePdfMain() {
    }

    public static void main(String[] args) throws IOException {
        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path outputPath = args != null && args.length > 0
                ? projectRoot.resolve(args[0]).normalize()
                : projectRoot.resolve(Paths.get("examples", "output", "sample-chart.pdf"));

        try (LocalNodeRenderService renderService = new LocalNodeRenderService(projectRoot)) {
            renderService.start();

            ChartSpec chart = buildSampleChart();
            ChartRenderer renderer = new HttpChartRenderer(renderService.getRenderEndpoint());
            EchartsFigure figure = EchartsFigure.of(chart)
                    .renderer(renderer)
                    .autoFormat()
                    .fitWidth()
                    .caption("Figure 1. Two-series line chart rendered as an iText figure element.")
                    .build();
            byte[] pdfBytes = new PdfChartWriter().writeSinglePage(figure);

            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("PDF generated at: " + outputPath);
            System.out.println("Render service log: " + renderService.getLogFile());
        }
    }

    private static ChartSpec buildSampleChart() {
        // The sample intentionally uses two lines so the generated PDF is visually obvious.
        return Charts.line()
                .size(960, 420)
                .layout(ChartLayouts.report())
                .title("End-to-End ECharts to PDF Demo")
                .xAxis(axis -> axis.name("Time (min)").range(0d, 60d).splitNumber(6))
                .yAxis(axis -> axis.name("mAU").autoRange().splitNumber(8))
                .series("Sample A", Arrays.asList(
                        new ChartPoint(0d, 1.0d),
                        new ChartPoint(10d, 4.5d),
                        new ChartPoint(20d, 9.2d),
                        new ChartPoint(30d, 7.1d),
                        new ChartPoint(40d, 12.4d),
                        new ChartPoint(50d, 8.8d),
                        new ChartPoint(60d, 10.1d)
                ), series -> series.smooth(Boolean.TRUE).lineWidth(1.8d))
                .series("Sample B", Arrays.asList(
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
}
