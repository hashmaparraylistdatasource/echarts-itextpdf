package io.github.echartsitext.layout;

import io.github.echartsitext.dsl.CartesianChartBuilder;
import io.github.echartsitext.spec.AxisTitleLayoutMode;

/**
 * Immutable, reusable chart layout profile.
 * It keeps spacing and placement defaults in one place so teams can define
 * report-wide layout conventions without scattering builder calls.
 */
public final class ChartLayoutProfile implements ChartLayoutPreset {
    private final Boolean legendShow;
    private final String legendOrient;
    private final String legendLeft;
    private final String legendTop;
    private final String gridLeft;
    private final String gridRight;
    private final String gridTop;
    private final String gridBottom;
    private final Boolean containLabel;
    private final AxisTitleLayoutMode xAxisTitleLayoutMode;
    private final AxisTitleLayoutMode yAxisTitleLayoutMode;

    private ChartLayoutProfile(Builder builder) {
        this.legendShow = builder.legendShow;
        this.legendOrient = builder.legendOrient;
        this.legendLeft = builder.legendLeft;
        this.legendTop = builder.legendTop;
        this.gridLeft = builder.gridLeft;
        this.gridRight = builder.gridRight;
        this.gridTop = builder.gridTop;
        this.gridBottom = builder.gridBottom;
        this.containLabel = builder.containLabel;
        this.xAxisTitleLayoutMode = builder.xAxisTitleLayoutMode;
        this.yAxisTitleLayoutMode = builder.yAxisTitleLayoutMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public Boolean getLegendShow() {
        return legendShow;
    }

    public String getLegendOrient() {
        return legendOrient;
    }

    public String getLegendLeft() {
        return legendLeft;
    }

    public String getLegendTop() {
        return legendTop;
    }

    public String getGridLeft() {
        return gridLeft;
    }

    public String getGridRight() {
        return gridRight;
    }

    public String getGridTop() {
        return gridTop;
    }

    public String getGridBottom() {
        return gridBottom;
    }

    public Boolean getContainLabel() {
        return containLabel;
    }

    public AxisTitleLayoutMode getXAxisTitleLayoutMode() {
        return xAxisTitleLayoutMode;
    }

    public AxisTitleLayoutMode getYAxisTitleLayoutMode() {
        return yAxisTitleLayoutMode;
    }

    @Override
    public void apply(CartesianChartBuilder builder) {
        builder.legend(legend -> {
            if (legendShow != null) {
                legend.show(legendShow.booleanValue());
            }
            if (legendOrient != null) {
                legend.orient(legendOrient);
            }
            if (legendLeft != null) {
                legend.left(legendLeft);
            }
            if (legendTop != null) {
                legend.top(legendTop);
            }
        });
        builder.grid(grid -> {
            if (gridLeft != null) {
                grid.left(gridLeft);
            }
            if (gridRight != null) {
                grid.right(gridRight);
            }
            if (gridTop != null) {
                grid.top(gridTop);
            }
            if (gridBottom != null) {
                grid.bottom(gridBottom);
            }
            if (containLabel != null) {
                grid.containLabel(containLabel);
            }
        });
        if (xAxisTitleLayoutMode != null) {
            builder.allXAxes(axis -> axis.titleLayoutMode(xAxisTitleLayoutMode));
        }
        if (yAxisTitleLayoutMode != null) {
            builder.allYAxes(axis -> axis.titleLayoutMode(yAxisTitleLayoutMode));
        }
    }

    /**
     * Builder for chart layout profiles.
     */
    public static final class Builder {
        private Boolean legendShow;
        private String legendOrient;
        private String legendLeft;
        private String legendTop;
        private String gridLeft;
        private String gridRight;
        private String gridTop;
        private String gridBottom;
        private Boolean containLabel;
        private AxisTitleLayoutMode xAxisTitleLayoutMode;
        private AxisTitleLayoutMode yAxisTitleLayoutMode;

        private Builder() {
        }

        private Builder(ChartLayoutProfile profile) {
            this.legendShow = profile.legendShow;
            this.legendOrient = profile.legendOrient;
            this.legendLeft = profile.legendLeft;
            this.legendTop = profile.legendTop;
            this.gridLeft = profile.gridLeft;
            this.gridRight = profile.gridRight;
            this.gridTop = profile.gridTop;
            this.gridBottom = profile.gridBottom;
            this.containLabel = profile.containLabel;
            this.xAxisTitleLayoutMode = profile.xAxisTitleLayoutMode;
            this.yAxisTitleLayoutMode = profile.yAxisTitleLayoutMode;
        }

        public Builder legendShow(Boolean legendShow) {
            this.legendShow = legendShow;
            return this;
        }

        public Builder legendOrient(String legendOrient) {
            this.legendOrient = legendOrient;
            return this;
        }

        public Builder legendLeft(String legendLeft) {
            this.legendLeft = legendLeft;
            return this;
        }

        public Builder legendTop(String legendTop) {
            this.legendTop = legendTop;
            return this;
        }

        public Builder gridLeft(String gridLeft) {
            this.gridLeft = gridLeft;
            return this;
        }

        public Builder gridRight(String gridRight) {
            this.gridRight = gridRight;
            return this;
        }

        public Builder gridTop(String gridTop) {
            this.gridTop = gridTop;
            return this;
        }

        public Builder gridBottom(String gridBottom) {
            this.gridBottom = gridBottom;
            return this;
        }

        public Builder containLabel(Boolean containLabel) {
            this.containLabel = containLabel;
            return this;
        }

        public Builder xAxisTitleLayoutMode(AxisTitleLayoutMode xAxisTitleLayoutMode) {
            this.xAxisTitleLayoutMode = xAxisTitleLayoutMode;
            return this;
        }

        public Builder yAxisTitleLayoutMode(AxisTitleLayoutMode yAxisTitleLayoutMode) {
            this.yAxisTitleLayoutMode = yAxisTitleLayoutMode;
            return this;
        }

        public ChartLayoutProfile build() {
            return new ChartLayoutProfile(this);
        }
    }
}
