package io.github.echartsitext.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.render.RenderedChart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Converts rendered chart bytes into a single-page PDF document using iText.
 */
public final class PdfChartWriter {
    public byte[] writeSinglePage(RenderedChart chart) {
        Objects.requireNonNull(chart, "chart");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            com.itextpdf.kernel.geom.Rectangle area = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
            document.add(RenderedChartElementFactory.toFigure(chart, pdfDocument, null, area.getWidth(), area.getHeight()));

            document.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write chart into PDF", e);
        }
    }

    public byte[] writeSinglePage(EchartsFigure figure) {
        return writeFigures(Collections.singletonList(Objects.requireNonNull(figure, "figure")));
    }

    public byte[] writeFigures(List<EchartsFigure> figures) {
        List<EchartsFigure> safeFigures = ValidationSupport.mutableListCopy(figures, "figures");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            for (int i = 0; i < safeFigures.size(); i++) {
                if (i > 0) {
                    document.add(new AreaBreak());
                }
                document.add(safeFigures.get(i));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write chart figures into PDF", e);
        }
    }
}
