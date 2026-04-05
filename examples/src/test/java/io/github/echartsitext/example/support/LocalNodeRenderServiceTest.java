package io.github.echartsitext.example.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalNodeRenderServiceTest {
    @Test
    void shouldParseHealthPayload() throws IOException {
        String json = "{"
                + "\"status\":\"ok\","
                + "\"service\":\"echarts-itextpdf-render-service\","
                + "\"protocolVersion\":2,"
                + "\"capabilities\":[\"svg\",\"png\",\"echarts-gl\"],"
                + "\"browserAvailable\":true"
                + "}";

        LocalNodeRenderService.ServiceHealth health = LocalNodeRenderService.parseHealthPayload(json);

        assertEquals("ok", health.status);
        assertEquals("echarts-itextpdf-render-service", health.service);
        assertEquals(2, health.protocolVersion);
        assertEquals(Arrays.asList("svg", "png", "echarts-gl"), health.capabilities);
        assertTrue(health.browserAvailable);
    }

    @Test
    void shouldRejectOldProtocolHealthPayload() {
        LocalNodeRenderService.ServiceHealth health = new LocalNodeRenderService.ServiceHealth();
        health.status = "ok";
        health.service = "echarts-itextpdf-render-service";
        health.protocolVersion = 1;
        health.capabilities = Arrays.asList("svg");

        assertFalse(LocalNodeRenderService.isCompatible(health));
    }

    @Test
    void shouldAcceptExpectedProtocolHealthPayload() {
        LocalNodeRenderService.ServiceHealth health = new LocalNodeRenderService.ServiceHealth();
        health.status = "ok";
        health.service = "echarts-itextpdf-render-service";
        health.protocolVersion = 2;
        health.capabilities = Arrays.asList("svg", "png");

        assertTrue(LocalNodeRenderService.isCompatible(health));
    }

    @Test
    void shouldRejectMissingRequiredCapabilities() {
        LocalNodeRenderService.ServiceHealth health = new LocalNodeRenderService.ServiceHealth();
        health.status = "ok";
        health.service = "echarts-itextpdf-render-service";
        health.protocolVersion = 2;
        health.capabilities = Arrays.asList("svg");

        assertFalse(LocalNodeRenderService.isCompatible(health, "svg", "png"));
    }

    @Test
    void shouldReportPortAvailability() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        try {
            int occupiedPort = socket.getLocalPort();
            assertFalse(LocalNodeRenderService.canBindPort(occupiedPort));
            int freePort = LocalNodeRenderService.findFreePort();
            assertTrue(freePort > 0);
            assertNotEquals(occupiedPort, freePort);
        } finally {
            socket.close();
        }
    }
}
