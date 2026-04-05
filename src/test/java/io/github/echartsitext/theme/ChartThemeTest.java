package io.github.echartsitext.theme;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.json.JacksonEchartsOptionWriter;
import io.github.echartsitext.spec.AxisTitleLayoutMode;
import io.github.echartsitext.spec.ChartSpec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ChartThemeTest {
    @Test
    void shouldSupportIncrementalThemeCustomization() throws Exception {
        ChartTheme theme = ChartTheme.report().toBuilder()
                .defaultFontSize(14)
                .palette("#111111", "#222222")
                .gridRight("12%")
                .build();

        ChartSpec spec = Charts.line()
                .theme(theme)
                .xAxis(axis -> axis.name("Time (min)").titleLayoutMode(AxisTitleLayoutMode.MIDDLE_SAFE))
                .yAxis(axis -> axis.name("mAU"))
                .build();

        JsonNode root = new ObjectMapper().readTree(new JacksonEchartsOptionWriter().write(spec));

        assertEquals("#111111", root.path("color").get(0).asText());
        assertEquals(14, root.path("xAxis").get(0).path("axisLabel").path("fontSize").asInt());
        assertEquals("12%", root.path("grid").path("right").asText());
    }

    @Test
    void shouldDefensivelyCopyPaletteInput() {
        List<String> palette = new ArrayList<String>(Arrays.asList("#111111", "#222222"));
        ChartTheme theme = new ChartTheme("Noto Sans", 12, 1.2d, "#222222", "#222222",
                "#ffffff", palette, "8%", "6%", "10%", "16%");

        palette.set(0, "#999999");

        assertIterableEquals(Arrays.asList("#111111", "#222222"), theme.getPalette());
    }

    @Test
    void shouldRejectInvalidThemeMeasurementsAndPaletteEntries() {
        assertThrows(IllegalArgumentException.class, () -> ChartTheme.builder().defaultFontSize(0));
        assertThrows(IllegalArgumentException.class, () -> ChartTheme.builder().defaultLineWidth(-0.1d));
        assertThrows(NullPointerException.class,
                () -> ChartTheme.builder().palette(Arrays.asList("#111111", null)).build());
    }
}
