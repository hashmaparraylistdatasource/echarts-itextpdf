package io.github.echartsitext.option;

import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.theme.ChartTheme;

/**
 * Default implementation that writes the built-in chart structure before optional modules run.
 */
final class DefaultOptionComposer implements OptionComposer {
    private final MetadataOptionFactory metadataFactory = new MetadataOptionFactory();
    private final AxisOptionFactory axisFactory = new AxisOptionFactory();
    private final SeriesOptionFactory seriesFactory = new SeriesOptionFactory();

    @Override
    public OptionTree compose(ChartSpec spec) {
        ChartTheme theme = resolveTheme(spec);
        ChartContext context = new ChartContext(spec, theme);
        OptionTree tree = new OptionTree();

        writeRootOptions(tree, spec, theme);
        writeStructuredOptions(tree, spec, theme);
        applyExtensions(tree, spec);
        applyModules(tree, context, spec);
        return tree;
    }

    private ChartTheme resolveTheme(ChartSpec spec) {
        return spec.getTheme() == null ? ChartTheme.report() : spec.getTheme();
    }

    private void writeRootOptions(OptionTree tree, ChartSpec spec, ChartTheme theme) {
        // Animation is disabled by default because PDF export wants deterministic output.
        tree.put("animation", Boolean.FALSE);
        tree.putIfNotNull("backgroundColor", resolveBackgroundColor(spec, theme));
        tree.putIfNotNull("color", theme.getPalette());
    }

    private String resolveBackgroundColor(ChartSpec spec, ChartTheme theme) {
        return spec.getBackgroundColor() == null ? theme.getBackgroundColor() : spec.getBackgroundColor();
    }

    private void writeStructuredOptions(OptionTree tree, ChartSpec spec, ChartTheme theme) {
        metadataFactory.writeBaseMetadata(tree, spec, theme);
        if (!spec.getXAxes().isEmpty()) {
            tree.put("xAxis", axisFactory.buildAxes(spec.getXAxes(), theme, true));
        }
        if (!spec.getYAxes().isEmpty()) {
            tree.put("yAxis", axisFactory.buildAxes(spec.getYAxes(), theme, false));
        }
        if (spec.getXAxis3D() != null) {
            tree.put("xAxis3D", spec.getXAxis3D().toOptionMap());
        }
        if (spec.getYAxis3D() != null) {
            tree.put("yAxis3D", spec.getYAxis3D().toOptionMap());
        }
        if (spec.getZAxis3D() != null) {
            tree.put("zAxis3D", spec.getZAxis3D().toOptionMap());
        }
        if (spec.getGrid3D() != null) {
            tree.put("grid3D", spec.getGrid3D().toOptionMap());
        }
        if (!spec.getPieSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildPieSeries(spec.getPieSeries()));
            return;
        }
        if (spec.getRadar() != null) {
            tree.put("radar", spec.getRadar().toOptionMap());
        }
        if (!spec.getRadarSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildRadarSeries(spec.getRadarSeries()));
            return;
        }
        if (!spec.getFunnelSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildFunnelSeries(spec.getFunnelSeries()));
            return;
        }
        if (!spec.getTreeSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildTreeSeries(spec.getTreeSeries()));
            return;
        }
        if (!spec.getTreemapSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildTreemapSeries(spec.getTreemapSeries()));
            return;
        }
        if (!spec.getSunburstSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildSunburstSeries(spec.getSunburstSeries()));
            return;
        }
        if (!spec.getBoxplotSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildBoxplotSeries(spec.getBoxplotSeries()));
            return;
        }
        if (!spec.getHeatmapSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildHeatmapSeries(spec.getHeatmapSeries()));
            return;
        }
        if (!spec.getCandlestickSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildCandlestickSeries(spec.getCandlestickSeries()));
            return;
        }
        if (!spec.getBar3DSeries().isEmpty()) {
            tree.put("series", seriesFactory.buildBar3DSeries(spec.getBar3DSeries()));
            return;
        }
        tree.put("series", seriesFactory.buildSeries(spec.getSeries(), theme));
    }

    private void applyExtensions(OptionTree tree, ChartSpec spec) {
        tree.putAll(spec.getExtensions());
    }

    private void applyModules(OptionTree tree, ChartContext context, ChartSpec spec) {
        for (OptionModule module : spec.getModules()) {
            module.apply(tree, context);
        }
    }
}
