package io.github.echartsitext.option;

import io.github.echartsitext.spec.AxisSpec;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.FunnelSeriesSpec;
import io.github.echartsitext.spec.FunnelSliceSpec;
import io.github.echartsitext.spec.HierarchyNodeSpec;
import io.github.echartsitext.spec.PieSeriesSpec;
import io.github.echartsitext.spec.PieSliceSpec;
import io.github.echartsitext.spec.GridSpec;
import io.github.echartsitext.spec.LegendSpec;
import io.github.echartsitext.spec.RadarSeriesSpec;
import io.github.echartsitext.spec.RadarValueSpec;
import io.github.echartsitext.spec.SeriesSpec;
import io.github.echartsitext.spec.SunburstSeriesSpec;
import io.github.echartsitext.spec.TreeSeriesSpec;
import io.github.echartsitext.spec.TitleSpec;
import io.github.echartsitext.spec.TreemapSeriesSpec;
import io.github.echartsitext.spec.TooltipSpec;
import io.github.echartsitext.theme.ChartTheme;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the top-level metadata blocks such as title, legend, tooltip, and grid.
 */
final class MetadataOptionFactory {
    private static final String SAFE_LEFT_MARGIN = "10%";
    private static final String SAFE_RIGHT_MARGIN = "14%";
    private static final String SAFE_TOP_MARGIN = "14%";
    private static final String SAFE_BOTTOM_MARGIN = "20%";

    private final AxisOptionFactory axisOptionFactory = new AxisOptionFactory();

    Map<String, Object> buildTitle(TitleSpec spec, ChartTheme theme) {
        if (spec == null) {
            return null;
        }
        return OptionMapBuilder.create()
                .put("text", spec.getText())
                .put("show", spec.isShow())
                .putIfNotNull("left", spec.getLeft())
                .put("textStyle", OptionSupport.toTextStyle(spec.getTextStyle(), theme, true))
                .build();
    }

    Map<String, Object> buildLegend(LegendSpec spec, ChartSpec chartSpec, ChartTheme theme) {
        if (spec == null) {
            return null;
        }
        return OptionMapBuilder.create()
                .put("show", spec.isShow())
                .putIfNotNull("orient", spec.getOrient())
                .putIfNotNull("left", spec.getLeft())
                .putIfNotNull("top", spec.getTop())
                .put("data", resolveLegendData(chartSpec))
                .put("textStyle", OptionSupport.toTextStyle(spec.getTextStyle(), theme, false))
                .putAll(spec.getExtensions())
                .build();
    }

    Map<String, Object> buildTooltip(TooltipSpec spec) {
        if (spec == null) {
            return null;
        }
        OptionMapBuilder tooltip = OptionMapBuilder.create()
                .put("show", spec.isShow())
                .putIfNotNull("trigger", spec.getTrigger())
                .putIfNotNull("formatter", spec.getFormatter())
                .putAll(spec.getExtensions());
        if (!spec.getAxisPointer().isEmpty()) {
            tooltip.put("axisPointer", spec.getAxisPointer());
        }
        return tooltip.build();
    }

    Map<String, Object> buildGrid(ChartSpec chartSpec, GridSpec spec, ChartTheme theme) {
        LinkedHashMap<String, Object> grid = new LinkedHashMap<String, Object>();
        GridMarginResolution resolution = GridMarginResolution.resolve(spec, theme);

        applyEndpointTitlePadding(chartSpec, resolution);
        writeResolvedMargins(grid, resolution);
        OptionSupport.putIfNotNull(grid, "containLabel", spec == null ? Boolean.TRUE : spec.getContainLabel());
        applyGridVisualOptions(grid, spec);
        return grid;
    }

    void writeBaseMetadata(OptionTree tree, ChartSpec spec, ChartTheme theme) {
        tree.putIfNotNull("title", buildTitle(spec.getTitle(), theme));
        tree.putIfNotNull("tooltip", buildTooltip(spec.getTooltip()));
        tree.putIfNotNull("legend", buildLegend(spec.getLegend(), spec, theme));
        if (usesCartesianGrid(spec)) {
            tree.putIfNotNull("grid", buildGrid(spec, spec.getGrid(), theme));
        }
    }

    private List<String> resolveLegendData(ChartSpec chartSpec) {
        if (!chartSpec.getPieSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (PieSeriesSpec pieSeriesSpec : chartSpec.getPieSeries()) {
                for (PieSliceSpec slice : pieSeriesSpec.getData()) {
                    names.add(slice.getName());
                }
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getRadarSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (RadarSeriesSpec radarSeriesSpec : chartSpec.getRadarSeries()) {
                for (RadarValueSpec value : radarSeriesSpec.getData()) {
                    names.add(value.getName());
                }
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getFunnelSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (FunnelSeriesSpec funnelSeriesSpec : chartSpec.getFunnelSeries()) {
                for (FunnelSliceSpec slice : funnelSeriesSpec.getData()) {
                    names.add(slice.getName());
                }
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getTreeSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (TreeSeriesSpec treeSeriesSpec : chartSpec.getTreeSeries()) {
                collectTopLevelHierarchyNames(names, treeSeriesSpec.getData());
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getTreemapSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (TreemapSeriesSpec treemapSeriesSpec : chartSpec.getTreemapSeries()) {
                collectTopLevelHierarchyNames(names, treemapSeriesSpec.getData());
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getSunburstSeries().isEmpty()) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            for (SunburstSeriesSpec sunburstSeriesSpec : chartSpec.getSunburstSeries()) {
                collectTopLevelHierarchyNames(names, sunburstSeriesSpec.getData());
            }
            return new ArrayList<String>(names);
        }
        if (!chartSpec.getBar3DSeries().isEmpty()) {
            return chartSpec.getBar3DSeries().stream()
                    .map(series -> (String) series.toOptionMap().get("name"))
                    .collect(Collectors.toList());
        }
        if (!chartSpec.getBoxplotSeries().isEmpty()) {
            return chartSpec.getBoxplotSeries().stream()
                    .map(series -> (String) series.toOptionMap().get("name"))
                    .collect(Collectors.toList());
        }
        if (!chartSpec.getHeatmapSeries().isEmpty()) {
            return chartSpec.getHeatmapSeries().stream()
                    .map(series -> (String) series.toOptionMap().get("name"))
                    .collect(Collectors.toList());
        }
        if (!chartSpec.getCandlestickSeries().isEmpty()) {
            return chartSpec.getCandlestickSeries().stream()
                    .map(series -> (String) series.toOptionMap().get("name"))
                    .collect(Collectors.toList());
        }
        return chartSpec.getSeries().stream().map(SeriesSpec::getName).collect(Collectors.toList());
    }

    private void collectTopLevelHierarchyNames(LinkedHashSet<String> names, List<HierarchyNodeSpec> nodes) {
        for (HierarchyNodeSpec node : nodes) {
            names.add(node.getName());
        }
    }

    private boolean usesCartesianGrid(ChartSpec spec) {
        return !spec.getXAxes().isEmpty()
                || !spec.getYAxes().isEmpty()
                || !spec.getSeries().isEmpty()
                || !spec.getBoxplotSeries().isEmpty()
                || !spec.getHeatmapSeries().isEmpty()
                || !spec.getCandlestickSeries().isEmpty()
                || spec.getGrid() != null;
    }

    private boolean hasEndpointTitle(List<AxisSpec> axes, boolean horizontalAxis) {
        for (AxisSpec axis : axes) {
            if (axis.getName() == null || axis.getName().trim().length() == 0) {
                continue;
            }
            if ("end".equalsIgnoreCase(axisOptionFactory.resolveNameLocation(axis, horizontalAxis))) {
                return true;
            }
        }
        return false;
    }

    private void applyEndpointTitlePadding(ChartSpec chartSpec, GridMarginResolution resolution) {
        if (hasEndpointTitle(chartSpec.getYAxes(), false)) {
            resolution.ensureLeft(SAFE_LEFT_MARGIN);
            resolution.ensureTop(SAFE_TOP_MARGIN);
        }
        if (hasEndpointTitle(chartSpec.getXAxes(), true)) {
            resolution.ensureRight(SAFE_RIGHT_MARGIN);
            resolution.ensureBottom(SAFE_BOTTOM_MARGIN);
        }
    }

    private void writeResolvedMargins(Map<String, Object> grid, GridMarginResolution resolution) {
        OptionSupport.putIfNotNull(grid, "left", resolution.getLeft());
        OptionSupport.putIfNotNull(grid, "right", resolution.getRight());
        OptionSupport.putIfNotNull(grid, "top", resolution.getTop());
        OptionSupport.putIfNotNull(grid, "bottom", resolution.getBottom());
    }

    private void applyGridVisualOptions(Map<String, Object> grid, GridSpec spec) {
        if (spec == null) {
            return;
        }
        OptionSupport.putIfNotNull(grid, "showBackground", spec.getShowBackground());
        OptionSupport.putIfNotNull(grid, "backgroundColor", spec.getBackgroundColor());
        grid.putAll(spec.getExtensions());
    }

    private static String ensurePercentMargin(String current, String requiredMinimum) {
        if (current == null) {
            return requiredMinimum;
        }
        if (!current.endsWith("%") || !requiredMinimum.endsWith("%")) {
            return current;
        }
        try {
            double currentValue = Double.parseDouble(current.substring(0, current.length() - 1));
            double requiredValue = Double.parseDouble(requiredMinimum.substring(0, requiredMinimum.length() - 1));
            return currentValue >= requiredValue ? current : requiredMinimum;
        } catch (NumberFormatException ignored) {
            return current;
        }
    }

    /**
     * Keeps track of which grid edges came from automatic defaults so safe-spacing logic
     * only adjusts values that the caller did not pin explicitly.
     */
    private static final class GridMarginResolution {
        private String left;
        private String right;
        private String top;
        private String bottom;
        private final boolean autoLeft;
        private final boolean autoRight;
        private final boolean autoTop;
        private final boolean autoBottom;

        private GridMarginResolution(String left, String right, String top, String bottom,
                                     boolean autoLeft, boolean autoRight, boolean autoTop, boolean autoBottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.autoLeft = autoLeft;
            this.autoRight = autoRight;
            this.autoTop = autoTop;
            this.autoBottom = autoBottom;
        }

        static GridMarginResolution resolve(GridSpec spec, ChartTheme theme) {
            boolean autoLeft = spec == null || spec.getLeft() == null;
            boolean autoRight = spec == null || spec.getRight() == null;
            boolean autoTop = spec == null || spec.getTop() == null;
            boolean autoBottom = spec == null || spec.getBottom() == null;
            return new GridMarginResolution(
                    autoLeft ? theme.getGridLeft() : spec.getLeft(),
                    autoRight ? theme.getGridRight() : spec.getRight(),
                    autoTop ? theme.getGridTop() : spec.getTop(),
                    autoBottom ? theme.getGridBottom() : spec.getBottom(),
                    autoLeft, autoRight, autoTop, autoBottom
            );
        }

        String getLeft() {
            return left;
        }

        String getRight() {
            return right;
        }

        String getTop() {
            return top;
        }

        String getBottom() {
            return bottom;
        }

        void ensureLeft(String minimum) {
            if (autoLeft) {
                left = ensurePercentMargin(left, minimum);
            }
        }

        void ensureRight(String minimum) {
            if (autoRight) {
                right = ensurePercentMargin(right, minimum);
            }
        }

        void ensureTop(String minimum) {
            if (autoTop) {
                top = ensurePercentMargin(top, minimum);
            }
        }

        void ensureBottom(String minimum) {
            if (autoBottom) {
                bottom = ensurePercentMargin(bottom, minimum);
            }
        }
    }
}
