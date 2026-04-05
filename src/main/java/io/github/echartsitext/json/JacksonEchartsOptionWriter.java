package io.github.echartsitext.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.option.OptionTrees;
import io.github.echartsitext.spec.ChartSpec;

import java.util.Map;

/**
 * Default option writer backed by Jackson and a composable option pipeline.
 */
public final class JacksonEchartsOptionWriter implements EchartsOptionWriter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public JacksonEchartsOptionWriter() {
    }

    @Override
    public String write(ChartSpec spec) {
        try {
            return OBJECT_MAPPER.writeValueAsString(writeTree(spec));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize ECharts option", e);
        }
    }

    @Override
    public Map<String, Object> writeTree(ChartSpec spec) {
        return OptionTrees.composeToMap(spec);
    }
}
