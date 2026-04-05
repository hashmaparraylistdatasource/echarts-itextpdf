package io.github.echartsitext.layout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.json.JacksonEchartsOptionWriter;
import io.github.echartsitext.spec.AxisTitleLayoutMode;
import io.github.echartsitext.spec.ChartSpec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChartLayoutProfileTest {
    @Test
    void shouldAllowBuiltInLayoutProfilesToBeOverriddenIncrementally() throws Exception {
        ChartLayoutProfile profile = ChartLayouts.report().toBuilder()
                .legendTop("top")
                .legendLeft("right")
                .gridRight("22%")
                .xAxisTitleLayoutMode(AxisTitleLayoutMode.MIDDLE_SAFE)
                .build();

        ChartSpec spec = Charts.line()
                .layout(profile)
                .xAxis(axis -> axis.name("Time (min)"))
                .yAxis(axis -> axis.name("mAU"))
                .build();

        JsonNode root = new ObjectMapper().readTree(new JacksonEchartsOptionWriter().write(spec));

        assertEquals("right", root.path("legend").path("left").asText());
        assertEquals("top", root.path("legend").path("top").asText());
        assertEquals("22%", root.path("grid").path("right").asText());
        assertEquals("middle", root.path("xAxis").get(0).path("nameLocation").asText());
        assertEquals("end", root.path("yAxis").get(0).path("nameLocation").asText());
    }

    @Test
    void shouldApplyAxisLayoutDefaultsToAdditionalAxes() throws Exception {
        ChartSpec spec = Charts.line()
                .layout(ChartLayouts.compact())
                .xAxis(axis -> axis.name("Primary X"))
                .yAxis(axis -> axis.name("Primary Y"))
                .addXAxis(axis -> axis.name("Secondary X"))
                .addYAxis(axis -> axis.name("Secondary Y"))
                .build();

        JsonNode root = new ObjectMapper().readTree(new JacksonEchartsOptionWriter().write(spec));

        assertEquals("middle", root.path("xAxis").get(0).path("nameLocation").asText());
        assertEquals("middle", root.path("xAxis").get(1).path("nameLocation").asText());
        assertEquals("middle", root.path("yAxis").get(0).path("nameLocation").asText());
        assertEquals("middle", root.path("yAxis").get(1).path("nameLocation").asText());
        assertEquals(90, root.path("yAxis").get(1).path("nameRotate").asInt());
    }
}
