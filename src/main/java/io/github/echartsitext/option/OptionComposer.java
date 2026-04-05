package io.github.echartsitext.option;

import io.github.echartsitext.spec.ChartSpec;

/**
 * Assembles the base chart AST plus extension modules into a single option tree.
 */
interface OptionComposer {
    /**
     * Builds the intermediate ECharts option tree for the given chart.
     */
    OptionTree compose(ChartSpec spec);
}
