package io.github.echartsitext.json;

import io.github.echartsitext.spec.ChartSpec;

import java.util.Map;

/**
 * Converts the strongly typed chart model into an ECharts option tree or JSON string.
 */
public interface EchartsOptionWriter {
    /**
     * Serializes the chart model into a JSON payload ready to be sent to a renderer.
     */
    String write(ChartSpec spec);

    /**
     * Produces the intermediate option tree so callers can inspect or post-process it.
     */
    Map<String, Object> writeTree(ChartSpec spec);
}
