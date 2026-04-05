package io.github.echartsitext.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.json.JacksonEchartsOptionWriter;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartSpec;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModuleValidationTest {
    @Test
    void shouldRejectInvalidDataZoomArguments() {
        assertThrows(IllegalArgumentException.class, () -> DataZoomModule.inside().xAxisIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> DataZoomModule.inside().start(-1));
        assertThrows(IllegalArgumentException.class, () -> DataZoomModule.inside().end(101));
        assertThrows(IllegalArgumentException.class, () -> DataZoomModule.inside().start(80).end(20).build());
        assertThrows(IllegalArgumentException.class, () -> DataZoomModule.inside().raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidVisualMapArguments() {
        assertThrows(IllegalArgumentException.class, () -> VisualMaps.continuous(10d, 1d).build());
        assertThrows(IllegalArgumentException.class, () -> VisualMaps.continuous(0d, 10d).dimension(-1));
        assertThrows(IllegalArgumentException.class, () -> VisualMaps.continuous(0d, 10d).seriesIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> VisualMaps.continuous(0d, 10d).inRange(" ", "value"));
        assertThrows(IllegalArgumentException.class, () -> VisualMaps.continuous(0d, 10d).raw(" ", Boolean.TRUE));
    }

    @Test
    void shouldRejectInvalidAnnotationArguments() {
        assertThrows(IllegalArgumentException.class, () -> Annotations.horizontalLine(-1, "Limit", 8.5d));
        assertThrows(IllegalArgumentException.class, () -> Annotations.horizontalLine(0, " ", 8.5d));
        assertThrows(NullPointerException.class, () -> Annotations.verticalLine(0, "Limit", null));
    }

    @Test
    void shouldDefensivelyCopyVisualMapNestedValues() throws Exception {
        List<String> colors = new ArrayList<String>(Arrays.asList("#006e54", "#bb1005"));
        ChartSpec spec = Charts.scatter()
                .xAxis(axis -> axis.name("X"))
                .yAxis(axis -> axis.name("Y"))
                .series("Points", Collections.singletonList(new ChartPoint(0d, 1d)))
                .module(VisualMaps.continuous(0d, 10d)
                        .dimension(1)
                        .inRange("color", colors)
                        .build())
                .build();

        colors.set(0, "#ffffff");

        JsonNode root = new ObjectMapper().readTree(new JacksonEchartsOptionWriter().write(spec));
        assertEquals("#006e54", root.path("visualMap").get(0).path("inRange").path("color").get(0).asText());
    }
}
