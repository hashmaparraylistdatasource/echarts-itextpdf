package io.github.echartsitext.pdf;

import io.github.echartsitext.render.ChartRenderer;
import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartSpec;

/**
 * Small orchestration service that connects the renderer with the PDF writer.
 */
public final class ChartPdfService {
    private final ChartRenderer chartRenderer;
    private final PdfChartWriter pdfChartWriter;

    public ChartPdfService(ChartRenderer chartRenderer, PdfChartWriter pdfChartWriter) {
        this.chartRenderer = chartRenderer;
        this.pdfChartWriter = pdfChartWriter;
    }

    public byte[] renderSinglePage(ChartSpec spec, ChartFormat format) {
        EchartsFigure figure = EchartsFigure.of(spec)
                .renderer(chartRenderer)
                .format(format)
                .build();
        return pdfChartWriter.writeSinglePage(figure);
    }
}
