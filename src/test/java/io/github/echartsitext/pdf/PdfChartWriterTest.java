package io.github.echartsitext.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.render.ChartRenderer;
import io.github.echartsitext.render.RenderedChart;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartPoint3D;
import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartSpec;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfChartWriterTest {
    @Test
    void shouldEmbedSvgIntoPdf() {
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"720\" height=\"320\" viewBox=\"0 0 720 320\">"
                + "<rect x=\"0\" y=\"0\" width=\"720\" height=\"320\" fill=\"#ffffff\"/>"
                + "<polyline points=\"40,260 160,120 280,180 400,90 520,130 640,70\" "
                + "fill=\"none\" stroke=\"#006e54\" stroke-width=\"3\"/>"
                + "</svg>";

        RenderedChart chart = new RenderedChart(
                ChartFormat.SVG,
                svg.getBytes(StandardCharsets.UTF_8),
                720,
                320
        );

        byte[] pdf = new PdfChartWriter().writeSinglePage(chart);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
    }

    @Test
    void shouldWriteFigureElementIntoPdf() {
        ChartSpec spec = Charts.line()
                .title("Figure API Demo")
                .series("A", Arrays.asList(
                        new ChartPoint(0d, 1d),
                        new ChartPoint(10d, 3d),
                        new ChartPoint(20d, 2d)
                ))
                .build();

        ChartRenderer renderer = (chartSpec, format) -> new RenderedChart(
                ChartFormat.SVG,
                ("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"720\" height=\"320\" viewBox=\"0 0 720 320\">"
                        + "<rect x=\"0\" y=\"0\" width=\"720\" height=\"320\" fill=\"#ffffff\"/>"
                        + "<polyline points=\"40,260 160,120 280,180 400,90 520,130 640,70\" "
                        + "fill=\"none\" stroke=\"#006e54\" stroke-width=\"3\"/>"
                        + "</svg>").getBytes(StandardCharsets.UTF_8),
                720,
                320
        );

        EchartsFigure figure = EchartsFigure.of(spec)
                .renderer(renderer)
                .autoFormat()
                .caption("Figure 1. Stubbed chart rendered through the figure API.")
                .build();

        byte[] pdf = new PdfChartWriter().writeSinglePage(figure);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
    }

    @Test
    void shouldAllowDirectDocumentAddForFigure() {
        ChartSpec spec = Charts.line()
                .title("Direct Add Demo")
                .series("A", Arrays.asList(
                        new ChartPoint(0d, 1d),
                        new ChartPoint(10d, 3d),
                        new ChartPoint(20d, 2d)
                ))
                .build();

        ChartRenderer renderer = (chartSpec, format) -> new RenderedChart(
                ChartFormat.SVG,
                ("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"720\" height=\"320\" viewBox=\"0 0 720 320\">"
                        + "<rect x=\"0\" y=\"0\" width=\"720\" height=\"320\" fill=\"#ffffff\"/>"
                        + "<polyline points=\"40,260 160,120 280,180 400,90 520,130 640,70\" "
                        + "fill=\"none\" stroke=\"#006e54\" stroke-width=\"3\"/>"
                        + "</svg>").getBytes(StandardCharsets.UTF_8),
                720,
                320
        );

        EchartsFigure figure = EchartsFigure.of(spec)
                .renderer(renderer)
                .autoFormat()
                .caption("Figure 1. Added directly to Document.")
                .build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        Document document = new Document(pdfDocument);
        document.add(figure);
        document.close();

        byte[] pdf = outputStream.toByteArray();
        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
    }

    @Test
    void shouldRejectInvalidPdfFigureInputs() {
        assertThrows(NullPointerException.class, () -> EchartsFigure.of(null));
        assertThrows(NullPointerException.class, () -> new PdfChartWriter().writeSinglePage((RenderedChart) null));
        assertThrows(NullPointerException.class, () -> new PdfChartWriter().writeSinglePage((EchartsFigure) null));
        assertThrows(NullPointerException.class, () -> new PdfChartWriter().writeFigures(Collections.singletonList(null)));
    }

    @Test
    void shouldAutoSelectPngForTypedThreeDimensionalCharts() {
        ChartSpec spec = Charts.bar3D()
                .title("3D Auto Format")
                .xAxis3D(axis -> axis.name("Temperature").categories(Arrays.asList("50C", "60C")))
                .yAxis3D(axis -> axis.name("Flow Rate").categories(Arrays.asList("0.8", "1.0")))
                .zAxis3D(axis -> axis.name("Yield"))
                .series("Yield Surface", Arrays.asList(
                        new ChartPoint3D(0, 0, 4.1d),
                        new ChartPoint3D(1, 1, 7.3d)
                ))
                .build();

        final ChartFormat[] capturedFormat = new ChartFormat[1];
        final byte[] png = Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgAq6Rt0AAAAASUVORK5CYII=");
        ChartRenderer renderer = (chartSpec, format) -> {
            capturedFormat[0] = format;
            return new RenderedChart(
                    ChartFormat.PNG,
                    png,
                    960,
                    540
            );
        };

        EchartsFigure figure = EchartsFigure.of(spec)
                .renderer(renderer)
                .autoFormat()
                .build();

        byte[] pdf = new PdfChartWriter().writeSinglePage(figure);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
        assertEquals(ChartFormat.PNG, capturedFormat[0]);
    }
}
