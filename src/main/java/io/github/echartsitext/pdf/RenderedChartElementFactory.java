package io.github.echartsitext.pdf;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.svg.converter.SvgConverter;
import io.github.echartsitext.render.RenderedChart;
import io.github.echartsitext.spec.ChartFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Converts rendered chart bytes into iText layout elements.
 */
final class RenderedChartElementFactory {
    static final float PX_TO_PT = 0.75f;
    static final float CAPTION_FONT_SIZE = 10f;
    static final float CAPTION_MARGIN_TOP = 8f;

    private RenderedChartElementFactory() {
    }

    static Image toImage(RenderedChart chart, PdfDocument pdfDocument) throws IOException {
        if (chart.getFormat() == ChartFormat.SVG) {
            return SvgConverter.convertToImage(new ByteArrayInputStream(chart.getBytes()), pdfDocument);
        }
        return new Image(ImageDataFactory.create(chart.getBytes()));
    }

    static Div toFigure(RenderedChart chart, PdfDocument pdfDocument, String caption) throws IOException {
        return toFigure(chart, pdfDocument, caption, chart.getWidth() * PX_TO_PT, chart.getHeight() * PX_TO_PT);
    }

    static Div toFigure(RenderedChart chart, PdfDocument pdfDocument, String caption, float maxWidth, float maxHeight) throws IOException {
        Div figure = new Div();
        figure.setKeepTogether(true);
        figure.setHorizontalAlignment(HorizontalAlignment.CENTER);
        figure.setTextAlignment(TextAlignment.CENTER);

        Image image = toImage(chart, pdfDocument);
        float naturalWidth = chart.getWidth() * PX_TO_PT;
        float naturalHeight = chart.getHeight() * PX_TO_PT;
        image.scaleToFit(Math.min(maxWidth, naturalWidth), Math.min(maxHeight, naturalHeight));
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        figure.add(image);

        Paragraph captionParagraph = createCaptionParagraph(caption);
        if (captionParagraph != null) {
            figure.add(captionParagraph);
        }
        return figure;
    }

    static Paragraph createCaptionParagraph(String caption) {
        if (caption == null || caption.trim().length() == 0) {
            return null;
        }
        Paragraph captionParagraph = new Paragraph(caption.trim());
        captionParagraph.setFontSize(CAPTION_FONT_SIZE);
        captionParagraph.setTextAlignment(TextAlignment.CENTER);
        captionParagraph.setMarginTop(CAPTION_MARGIN_TOP);
        captionParagraph.setMarginBottom(0f);
        return captionParagraph;
    }
}
