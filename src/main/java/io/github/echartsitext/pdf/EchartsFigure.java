package io.github.echartsitext.pdf;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.render.ChartRenderer;
import io.github.echartsitext.render.RenderedChart;
import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartSpec;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * A first-class iText block element that renders an ECharts chart when it is added to a document.
 */
public final class EchartsFigure extends BlockElement<EchartsFigure> {
    private final DefaultAccessibilityProperties tagProperties =
            new DefaultAccessibilityProperties(StandardRoles.FIGURE);

    private final ChartSpec chartSpec;
    private final ChartRenderer renderer;
    private final ChartFormat format;
    private final boolean autoFormat;
    private final String caption;
    private final FigureLayoutMode layoutMode;

    private transient RenderedChart cachedChart;
    private transient int cachedWidthPx = -1;
    private transient int cachedHeightPx = -1;
    private transient ChartFormat cachedFormat;

    private EchartsFigure(ChartSpec chartSpec, ChartRenderer renderer, ChartFormat format,
                          boolean autoFormat, String caption, FigureLayoutMode layoutMode) {
        this.chartSpec = Objects.requireNonNull(chartSpec, "chartSpec");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.format = Objects.requireNonNull(format, "format");
        this.autoFormat = autoFormat;
        this.caption = caption;
        this.layoutMode = Objects.requireNonNull(layoutMode, "layoutMode");
        setKeepTogether(true);
        setMarginBottom(16f);
        setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    public static Builder of(ChartSpec chartSpec) {
        return new Builder(Objects.requireNonNull(chartSpec, "chartSpec"));
    }

    public ChartSpec getChartSpec() {
        return chartSpec;
    }

    public ChartFormat getFormat() {
        return resolveFormat();
    }

    public String getCaption() {
        return caption;
    }

    /**
     * Convenience helper kept for callers who still prefer an explicit method over document.add(figure).
     */
    public void addTo(Document document) {
        document.add(this);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new EchartsFigureRenderer(this);
    }

    RenderedChart renderChart(int widthPx, int heightPx, ChartFormat renderFormat) {
        if (cachedChart == null
                || cachedWidthPx != widthPx
                || cachedHeightPx != heightPx
                || cachedFormat != renderFormat) {
            cachedChart = renderer.render(resizeChartSpec(widthPx, heightPx), renderFormat);
            cachedWidthPx = widthPx;
            cachedHeightPx = heightPx;
            cachedFormat = renderFormat;
        }
        return cachedChart;
    }

    boolean hasCaption() {
        return caption != null && caption.trim().length() > 0;
    }

    private ChartSpec resizeChartSpec(int width, int height) {
        ChartSpec.Builder builder = ChartSpec.builder(chartSpec.getChartType())
                .width(width)
                .height(height)
                .backgroundColor(chartSpec.getBackgroundColor())
                .theme(chartSpec.getTheme())
                .title(chartSpec.getTitle())
                .legend(chartSpec.getLegend())
                .tooltip(chartSpec.getTooltip())
                .grid(chartSpec.getGrid())
                .radar(chartSpec.getRadar())
                .xAxis3D(chartSpec.getXAxis3D())
                .yAxis3D(chartSpec.getYAxis3D())
                .zAxis3D(chartSpec.getZAxis3D())
                .grid3D(chartSpec.getGrid3D())
                .xAxes(chartSpec.getXAxes())
                .yAxes(chartSpec.getYAxes())
                .series(chartSpec.getSeries())
                .pieSeries(chartSpec.getPieSeries())
                .radarSeries(chartSpec.getRadarSeries())
                .funnelSeries(chartSpec.getFunnelSeries())
                .boxplotSeries(chartSpec.getBoxplotSeries())
                .heatmapSeries(chartSpec.getHeatmapSeries())
                .candlestickSeries(chartSpec.getCandlestickSeries())
                .bar3DSeries(chartSpec.getBar3DSeries());
        for (OptionModule module : chartSpec.getModules()) {
            builder.module(module);
        }
        for (Map.Entry<String, Object> entry : chartSpec.getExtensions().entrySet()) {
            builder.raw(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private ChartFormat resolveFormat() {
        if (!autoFormat) {
            return format;
        }
        return usesThreeDimensionalRendering(chartSpec) ? ChartFormat.PNG : ChartFormat.SVG;
    }

    private boolean usesThreeDimensionalRendering(ChartSpec spec) {
        if (spec.getChartType().isThreeDimensional()) {
            return true;
        }
        Map<String, Object> extensions = spec.getExtensions();
        if (extensions.containsKey("xAxis3D")
                || extensions.containsKey("yAxis3D")
                || extensions.containsKey("zAxis3D")
                || extensions.containsKey("grid3D")) {
            return true;
        }
        return containsThreeDimensionalSeries(extensions.get("series"));
    }

    @SuppressWarnings("unchecked")
    private boolean containsThreeDimensionalSeries(Object series) {
        if (series instanceof Map) {
            Object type = ((Map<String, Object>) series).get("type");
            return type instanceof String && ((String) type).endsWith("3D");
        }
        if (series instanceof java.util.List) {
            for (Object item : (java.util.List<Object>) series) {
                if (containsThreeDimensionalSeries(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Builder that keeps the iText adapter fluent and close to regular document assembly code.
     */
    public static final class Builder {
        private final ChartSpec chartSpec;
        private ChartRenderer renderer;
        private ChartFormat format = ChartFormat.SVG;
        private boolean autoFormat;
        private String caption;
        private FigureLayoutMode layoutMode = FigureLayoutMode.SHRINK_TO_FIT;

        private Builder(ChartSpec chartSpec) {
            this.chartSpec = chartSpec;
        }

        public Builder renderer(ChartRenderer renderer) {
            this.renderer = Objects.requireNonNull(renderer, "renderer");
            return this;
        }

        public Builder format(ChartFormat format) {
            this.format = Objects.requireNonNull(format, "format");
            this.autoFormat = false;
            return this;
        }

        public Builder autoFormat() {
            this.autoFormat = true;
            return this;
        }

        public Builder caption(String caption) {
            this.caption = caption;
            return this;
        }

        public Builder layoutMode(FigureLayoutMode layoutMode) {
            this.layoutMode = Objects.requireNonNull(layoutMode, "layoutMode");
            return this;
        }

        public Builder fitWidth() {
            return layoutMode(FigureLayoutMode.FIT_WIDTH);
        }

        public Builder shrinkToFit() {
            return layoutMode(FigureLayoutMode.SHRINK_TO_FIT);
        }

        public EchartsFigure build() {
            if (renderer == null) {
                throw new IllegalStateException("A ChartRenderer is required to materialize an EchartsFigure");
            }
            return new EchartsFigure(chartSpec, renderer, format, autoFormat, caption, layoutMode);
        }
    }

    /**
     * Custom block renderer that measures the figure during layout and draws the chart during the draw phase.
     * This keeps document.add(figure) ergonomic without reaching into iText's internal renderer API.
     */
    private static final class EchartsFigureRenderer extends BlockRenderer {
        private int renderWidthPx;
        private int renderHeightPx;
        private float contentHeightPt;
        private ChartFormat renderFormat;

        private EchartsFigureRenderer(EchartsFigure modelElement) {
            super(modelElement);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            Rectangle availableOuter = new Rectangle(layoutContext.getArea().getBBox());
            Rectangle availableInner = toInnerArea(availableOuter);
            if (availableInner.getWidth() < 1f || availableInner.getHeight() < 1f) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this);
            }

            EchartsFigure figure = (EchartsFigure) getModelElement();
            float captionHeight = measureCaptionHeight(figure, availableInner.getWidth(), layoutContext.getArea().getPageNumber());
            float naturalWidthPt = figure.chartSpec.getWidth() * RenderedChartElementFactory.PX_TO_PT;
            float naturalHeightPt = figure.chartSpec.getHeight() * RenderedChartElementFactory.PX_TO_PT;
            float availableImageHeight = availableInner.getHeight() - captionHeight;
            if (naturalWidthPt <= 0f || naturalHeightPt <= 0f || availableImageHeight < 1f) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this);
            }

            float scale = FigureLayoutSupport.resolveScale(
                    figure.layoutMode,
                    naturalWidthPt,
                    naturalHeightPt,
                    availableInner.getWidth(),
                    availableImageHeight
            );
            if (scale <= 0f) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this);
            }

            float imageHeightPt = naturalHeightPt * scale;
            this.contentHeightPt = imageHeightPt + captionHeight;
            this.renderWidthPx = Math.max(1, Math.round(figure.chartSpec.getWidth() * scale));
            this.renderHeightPx = Math.max(1, Math.round(figure.chartSpec.getHeight() * scale));
            this.renderFormat = figure.resolveFormat();

            Rectangle occupiedInner = new Rectangle(
                    availableInner.getX(),
                    availableInner.getY() + availableInner.getHeight() - contentHeightPt,
                    availableInner.getWidth(),
                    contentHeightPt
            );
            Rectangle occupiedOuter = toOuterArea(occupiedInner);
            this.occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), occupiedOuter);
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);

            EchartsFigure figure = (EchartsFigure) getModelElement();
            Rectangle outerBox = new Rectangle(getOccupiedAreaBBox());
            Rectangle innerBox = toInnerArea(outerBox);
            Rectangle contentBox = new Rectangle(
                    innerBox.getX(),
                    innerBox.getY() + innerBox.getHeight() - contentHeightPt,
                    innerBox.getWidth(),
                    contentHeightPt
            );

            try {
                RenderedChart renderedChart = figure.renderChart(renderWidthPx, renderHeightPx, renderFormat);
                Div content = RenderedChartElementFactory.toFigure(
                        renderedChart,
                        drawContext.getDocument(),
                        figure.getCaption(),
                        contentBox.getWidth(),
                        contentBox.getHeight()
                );
                com.itextpdf.layout.Canvas canvas =
                        new com.itextpdf.layout.Canvas(drawContext.getCanvas(), contentBox, drawContext.isTaggingEnabled());
                canvas.add(content);
                canvas.close();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to draw EchartsFigure into PDF", e);
            }
        }

        @Override
        public IRenderer getNextRenderer() {
            return new EchartsFigureRenderer((EchartsFigure) getModelElement());
        }

        private Rectangle toInnerArea(Rectangle area) {
            Rectangle innerArea = new Rectangle(area);
            applyMargins(innerArea, false);
            applyBorderBox(innerArea, false);
            applyPaddings(innerArea, false);
            return innerArea;
        }

        private Rectangle toOuterArea(Rectangle innerArea) {
            Rectangle outerArea = new Rectangle(innerArea);
            applyPaddings(outerArea, true);
            applyBorderBox(outerArea, true);
            applyMargins(outerArea, true);
            return outerArea;
        }

        private float measureCaptionHeight(EchartsFigure figure, float availableWidth, int pageNumber) {
            if (!figure.hasCaption()) {
                return 0f;
            }
            IRenderer captionRenderer = RenderedChartElementFactory.createCaptionParagraph(figure.getCaption()).createRendererSubTree();
            captionRenderer.setParent(this);
            Rectangle captionArea = new Rectangle(0f, 0f, Math.max(1f, availableWidth), 10_000f);
            LayoutResult captionLayout = captionRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, captionArea)));
            if (captionLayout.getStatus() == LayoutResult.NOTHING || captionRenderer.getOccupiedArea() == null) {
                return 0f;
            }
            return captionRenderer.getOccupiedArea().getBBox().getHeight();
        }
    }
}
