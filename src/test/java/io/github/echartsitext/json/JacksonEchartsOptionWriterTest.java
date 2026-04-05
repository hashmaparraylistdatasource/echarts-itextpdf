package io.github.echartsitext.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.layout.ChartLayouts;
import io.github.echartsitext.module.Annotations;
import io.github.echartsitext.module.ModuleContext;
import io.github.echartsitext.module.OptionModule;
import io.github.echartsitext.module.OptionTarget;
import io.github.echartsitext.module.OptionTargets;
import io.github.echartsitext.module.VisualMaps;
import io.github.echartsitext.module.Zooms;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartPoint3D;
import io.github.echartsitext.spec.CandlestickValue;
import io.github.echartsitext.spec.ChartSpec;
import io.github.echartsitext.spec.BoxplotValue;
import io.github.echartsitext.spec.HeatmapPoint;
import io.github.echartsitext.spec.HierarchyNodeSpec;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonEchartsOptionWriterTest {
    @Test
    void shouldGenerateCleanLineChartOption() throws Exception {
        ChartSpec spec = Charts.line()
                .size(720, 320)
                .title("Sample Chromatogram")
                .xAxis(axis -> axis.name("Time (min)").range(0d, 60d).splitNumber(10))
                .yAxis(axis -> axis.name("mAU").autoRange())
                .series("Sample A", Arrays.asList(
                        new ChartPoint(0d, 0d),
                        new ChartPoint(10d, 12.4d),
                        new ChartPoint(20d, 8.1d)
                ), series -> series.smooth(true).lineWidth(1.4))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("Sample Chromatogram", root.path("title").path("text").asText());
        assertEquals("Time (min)", root.path("xAxis").get(0).path("name").asText());
        assertEquals("mAU", root.path("yAxis").get(0).path("name").asText());
        assertEquals("end", root.path("xAxis").get(0).path("nameLocation").asText());
        assertEquals(28, root.path("xAxis").get(0).path("nameGap").asInt());
        assertEquals("left", root.path("xAxis").get(0).path("nameTextStyle").path("align").asText());
        assertEquals("left", root.path("xAxis").get(0).path("axisLabel").path("alignMinLabel").asText());
        assertEquals("right", root.path("xAxis").get(0).path("axisLabel").path("alignMaxLabel").asText());
        assertEquals("end", root.path("yAxis").get(0).path("nameLocation").asText());
        assertEquals(16, root.path("yAxis").get(0).path("nameGap").asInt());
        assertEquals(0, root.path("yAxis").get(0).path("nameRotate").asInt());
        assertEquals("right", root.path("yAxis").get(0).path("nameTextStyle").path("align").asText());
        assertEquals("bottom", root.path("yAxis").get(0).path("nameTextStyle").path("verticalAlign").asText());
        assertEquals("14%", root.path("grid").path("right").asText());
        assertEquals("14%", root.path("grid").path("top").asText());
        assertEquals("20%", root.path("grid").path("bottom").asText());
        assertEquals("line", root.path("series").get(0).path("type").asText());
        assertEquals(3, root.path("series").get(0).path("data").size());
        assertFalse(root.path("animation").asBoolean(true));
    }

    @Test
    void shouldApplyLayoutPreset() throws Exception {
        ChartSpec spec = Charts.line()
                .layout(ChartLayouts.compact())
                .xAxis(axis -> axis.name("Time (min)"))
                .yAxis(axis -> axis.name("mAU"))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("right", root.path("legend").path("left").asText());
        assertEquals("top", root.path("legend").path("top").asText());
        assertEquals("vertical", root.path("legend").path("orient").asText());
        assertEquals("18%", root.path("grid").path("right").asText());
        assertEquals("middle", root.path("xAxis").get(0).path("nameLocation").asText());
        assertEquals("middle", root.path("yAxis").get(0).path("nameLocation").asText());
        assertEquals(90, root.path("yAxis").get(0).path("nameRotate").asInt());
    }

    @Test
    void shouldApplyModulesDuringComposition() throws Exception {
        ChartSpec spec = Charts.line()
                .title("Module Demo")
                .xAxis(axis -> axis.name("Time (min)"))
                .yAxis(axis -> axis.name("mAU"))
                .series("Sample A", Arrays.asList(
                        new ChartPoint(0d, 0d),
                        new ChartPoint(10d, 12.4d),
                        new ChartPoint(20d, 8.1d)
                ))
                .module(Zooms.inside())
                .module(Annotations.horizontalLine("Threshold", 8.5d))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("inside", root.path("dataZoom").get(0).path("type").asText());
        assertEquals("Threshold", root.path("series").get(0).path("markLine").path("data").get(0).path("name").asText());
        assertEquals(8.5d, root.path("series").get(0).path("markLine").path("data").get(0).path("yAxis").asDouble(), 0.0001d);
    }

    @Test
    void shouldApplyVisualMapModule() throws Exception {
        ChartSpec spec = Charts.scatter()
                .title("Visual Map Demo")
                .xAxis(axis -> axis.name("X"))
                .yAxis(axis -> axis.name("Y"))
                .series("Points", Arrays.asList(
                        new ChartPoint(0d, 1d),
                        new ChartPoint(10d, 6d),
                        new ChartPoint(20d, 12d)
                ), series -> series.symbol("circle").symbolSize(10))
                .module(VisualMaps.continuous(0d, 12d)
                        .dimension(1)
                        .seriesIndex(0)
                        .calculable(Boolean.TRUE)
                        .left("right")
                        .top("middle")
                        .inRange("color", Arrays.asList("#006e54", "#bb1005"))
                        .build())
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("continuous", root.path("visualMap").get(0).path("type").asText());
        assertEquals(1, root.path("visualMap").get(0).path("dimension").asInt());
        assertEquals("right", root.path("visualMap").get(0).path("left").asText());
        assertEquals(2, root.path("visualMap").get(0).path("inRange").path("color").size());
    }

    @Test
    void shouldSupportCustomModulesWithoutDependingOnOptionInternals() throws Exception {
        ChartSpec spec = Charts.line()
                .title("Custom Module Demo")
                .xAxis(axis -> axis.name("X"))
                .yAxis(axis -> axis.name("Y"))
                .series("Points", Arrays.asList(
                        new ChartPoint(0d, 1d),
                        new ChartPoint(1d, 2d)
                ))
                .module(new OptionModule() {
                    @Override
                    public void apply(OptionTarget target, ModuleContext context) {
                        Map<String, Object> tooltip = OptionTargets.ensureMap(target, "tooltip");
                        tooltip.put("formatter", context.getSpec().getTitle().getText() + ": {c}");
                    }
                })
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("Custom Module Demo: {c}", root.path("tooltip").path("formatter").asText());
    }

    @Test
    void shouldGenerateTypedBar3DOption() throws Exception {
        ChartSpec spec = Charts.bar3D()
                .size(960, 540)
                .title("3D Bar Chart")
                .xAxis3D(axis -> axis.name("Temperature").categories(Arrays.asList("50C", "60C")))
                .yAxis3D(axis -> axis.name("Flow Rate").categories(Arrays.asList("0.8", "1.0")))
                .zAxis3D(axis -> axis.name("Yield"))
                .series("Yield Surface", Arrays.asList(
                        new ChartPoint3D(0, 0, 4.1d),
                        new ChartPoint3D(1, 1, 7.3d)
                ))
                .module(VisualMaps.continuous(0d, 8d)
                        .dimension(2)
                        .seriesIndex(0)
                        .build())
                .build();

        assertEquals("Temperature", spec.getXAxis3D().toOptionMap().get("name"));
        assertEquals(1, spec.getBar3DSeries().size());
        assertTrue(spec.getExtensions().isEmpty());

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("Temperature", root.path("xAxis3D").path("name").asText());
        assertEquals("category", root.path("xAxis3D").path("type").asText());
        assertEquals("0.8", root.path("yAxis3D").path("data").get(0).asText());
        assertEquals("bar3D", root.path("series").get(0).path("type").asText());
        assertEquals(4.1d, root.path("series").get(0).path("data").get(0).get(2).asDouble(), 0.0001d);
        assertEquals(22, root.path("grid3D").path("viewControl").path("alpha").asInt());
        assertEquals("continuous", root.path("visualMap").get(0).path("type").asText());
    }

    @Test
    void shouldGenerateTypedPieOption() throws Exception {
        ChartSpec spec = Charts.pie()
                .size(720, 420)
                .title("Pie Chart")
                .donut("35%", "70%")
                .slice("API", 42)
                .slice("Batch", 28)
                .slice("Manual", 14)
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("Pie Chart", root.path("title").path("text").asText());
        assertEquals("pie", root.path("series").get(0).path("type").asText());
        assertEquals("35%", root.path("series").get(0).path("radius").get(0).asText());
        assertEquals("API", root.path("legend").path("data").get(0).asText());
        assertEquals(3, root.path("series").get(0).path("data").size());
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedRadarOption() throws Exception {
        ChartSpec spec = Charts.radar()
                .title("Radar Chart")
                .indicator("Purity", 100d)
                .indicator("Yield", 100d)
                .indicator("Stability", 100d)
                .value("Batch A", Arrays.asList(92d, 86d, 88d))
                .value("Batch B", Arrays.asList(84d, 90d, 81d))
                .series(series -> series.areaOpacity(0.12d))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("radar", root.path("series").get(0).path("type").asText());
        assertEquals("Purity", root.path("radar").path("indicator").get(0).path("name").asText());
        assertEquals("Batch A", root.path("legend").path("data").get(0).asText());
        assertEquals(88d, root.path("series").get(0).path("data").get(0).path("value").get(2).asDouble(), 0.0001d);
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedFunnelOption() throws Exception {
        ChartSpec spec = Charts.funnel()
                .title("Funnel Chart")
                .slice("Leads", 1200)
                .slice("Qualified", 860)
                .slice("Proposal", 430)
                .slice("Won", 170)
                .series(series -> series.gap(4).sort("descending"))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("funnel", root.path("series").get(0).path("type").asText());
        assertEquals("Qualified", root.path("legend").path("data").get(1).asText());
        assertEquals(4, root.path("series").get(0).path("gap").asInt());
        assertEquals(170d, root.path("series").get(0).path("data").get(3).path("value").asDouble(), 0.0001d);
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedTreemapOption() throws Exception {
        ChartSpec spec = Charts.treemap()
                .title("Treemap Chart")
                .node("Operations", 42, node -> node
                        .child("API", 18)
                        .child("Batch", 12)
                        .child("Manual", 12))
                .node("Quality", 28, node -> node
                        .child("Review", 11)
                        .child("Audit", 9)
                        .child("Deviation", 8))
                .series(series -> series.leafDepth(1).breadcrumbShow(Boolean.TRUE))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("treemap", root.path("series").get(0).path("type").asText());
        assertEquals("Operations", root.path("series").get(0).path("data").get(0).path("name").asText());
        assertEquals(12d, root.path("series").get(0).path("data").get(0).path("children").get(2).path("value").asDouble(), 0.0001d);
        assertEquals(1, root.path("series").get(0).path("leafDepth").asInt());
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedTreeOption() throws Exception {
        ChartSpec spec = Charts.tree()
                .title("Tree Chart")
                .branch("Portfolio", node -> node
                        .branch("Automation", child -> child
                                .child("API", 18)
                                .child("Batch", 24))
                        .branch("Quality", child -> child
                                .child("Audit", 16)
                                .child("Review", 14)))
                .series(series -> series.orient("LR").symbolSize(12))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("tree", root.path("series").get(0).path("type").asText());
        assertEquals("Portfolio", root.path("series").get(0).path("data").get(0).path("name").asText());
        assertEquals("Automation", root.path("series").get(0).path("data").get(0).path("children").get(0).path("name").asText());
        assertEquals("LR", root.path("series").get(0).path("orient").asText());
        assertEquals(12, root.path("series").get(0).path("symbolSize").asInt());
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedSunburstOption() throws Exception {
        ChartSpec spec = Charts.sunburst()
                .title("Sunburst Chart")
                .branch("Portfolio", node -> node
                        .child("Automation", 42, child -> child
                                .child("API", 18)
                                .child("Batch", 24))
                        .child("Quality", 30, child -> child
                                .child("Audit", 16)
                                .child("Review", 14)))
                .series(series -> series.radius("10%", "78%").labelRotate("tangential"))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("sunburst", root.path("series").get(0).path("type").asText());
        assertEquals("Portfolio", root.path("series").get(0).path("data").get(0).path("name").asText());
        assertEquals("Automation", root.path("series").get(0).path("data").get(0).path("children").get(0).path("name").asText());
        assertEquals("10%", root.path("series").get(0).path("radius").get(0).asText());
        assertEquals("tangential", root.path("series").get(0).path("label").path("rotate").asText());
        assertTrue(root.path("grid").isMissingNode());
    }

    @Test
    void shouldGenerateTypedBoxplotOption() throws Exception {
        ChartSpec spec = Charts.boxplot()
                .title("Boxplot Chart")
                .categories(Arrays.asList("Lot A", "Lot B", "Lot C"))
                .series("Spread", Arrays.asList(
                        new BoxplotValue(7d, 9d, 11d, 13d, 16d),
                        new BoxplotValue(6d, 8d, 10d, 12d, 14d),
                        new BoxplotValue(8d, 10d, 12d, 14d, 17d)
                ), series -> series.boxWidth("40%", "72%"))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("boxplot", root.path("series").get(0).path("type").asText());
        assertEquals("Lot A", root.path("xAxis").get(0).path("data").get(0).asText());
        assertEquals(11d, root.path("series").get(0).path("data").get(0).get(2).asDouble(), 0.0001d);
        assertEquals("40%", root.path("series").get(0).path("boxWidth").get(0).asText());
        assertEquals("Spread", root.path("legend").path("data").get(0).asText());
    }

    @Test
    void shouldGenerateTypedHeatmapOption() throws Exception {
        ChartSpec spec = Charts.heatmap()
                .title("Heatmap Chart")
                .xCategories(Arrays.asList("Mon", "Tue", "Wed"))
                .yCategories(Arrays.asList("Morning", "Noon"))
                .series("Load", Arrays.asList(
                        new HeatmapPoint(0, 0, 11),
                        new HeatmapPoint(1, 0, 18),
                        new HeatmapPoint(2, 1, 26)
                ))
                .module(VisualMaps.continuous(0d, 30d).dimension(2).seriesIndex(0).build())
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("heatmap", root.path("series").get(0).path("type").asText());
        assertEquals("Mon", root.path("xAxis").get(0).path("data").get(0).asText());
        assertEquals("Morning", root.path("yAxis").get(0).path("data").get(0).asText());
        assertEquals(26d, root.path("series").get(0).path("data").get(2).get(2).asDouble(), 0.0001d);
        assertEquals("continuous", root.path("visualMap").get(0).path("type").asText());
    }

    @Test
    void shouldGenerateTypedCandlestickOption() throws Exception {
        ChartSpec spec = Charts.candlestick()
                .title("Candlestick Chart")
                .categories(Arrays.asList("2026-04-01", "2026-04-02", "2026-04-03"))
                .series("Price", Arrays.asList(
                        new CandlestickValue(20, 24, 18, 26),
                        new CandlestickValue(24, 22, 20, 25),
                        new CandlestickValue(22, 27, 21, 30)
                ))
                .build();

        String json = new JacksonEchartsOptionWriter().write(spec);
        JsonNode root = new ObjectMapper().readTree(json);

        assertEquals("candlestick", root.path("series").get(0).path("type").asText());
        assertEquals("2026-04-01", root.path("xAxis").get(0).path("data").get(0).asText());
        assertEquals("cross", root.path("tooltip").path("axisPointer").path("type").asText());
        assertEquals(30d, root.path("series").get(0).path("data").get(2).get(3).asDouble(), 0.0001d);
    }
}
