package io.github.echartsitext.render;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.echartsitext.dsl.Charts;
import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartPoint;
import io.github.echartsitext.spec.ChartSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpChartRendererTest {
    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldRenderSuccessfulResponse() {
        byte[] svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>".getBytes(StandardCharsets.UTF_8);
        startServer(exchange -> writeResponse(exchange, 200, "image/svg+xml", svg));

        HttpChartRenderer renderer = new HttpChartRenderer(renderEndpoint(), new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(2));

        RenderedChart renderedChart = renderer.render(sampleChart(), ChartFormat.SVG);

        assertEquals(ChartFormat.SVG, renderedChart.getFormat());
        assertArrayEquals(svg, renderedChart.getBytes());
        assertEquals(320, renderedChart.getWidth());
        assertEquals(160, renderedChart.getHeight());
    }

    @Test
    void shouldExposeServiceErrorDetails() {
        startServer(exchange -> writeResponse(exchange, 500, "application/json",
                "{\"error\":\"renderer exploded\"}".getBytes(StandardCharsets.UTF_8)));

        HttpChartRenderer renderer = new HttpChartRenderer(renderEndpoint(), new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(2));

        ChartRenderException exception = assertThrows(ChartRenderException.class,
                () -> renderer.render(sampleChart(), ChartFormat.SVG));

        assertEquals(RenderFailureKind.SERVICE_ERROR, exception.getKind());
        assertEquals(Integer.valueOf(500), exception.getStatusCode());
        assertNotNull(exception.getResponseBody());
        assertTrue(exception.getResponseBody().contains("renderer exploded"));
        assertTrue(exception.getMessage().contains("HTTP 500"));
    }

    @Test
    void shouldExposeConnectionFailureDetails() throws IOException {
        int unusedPort;
        ServerSocket socket = new ServerSocket(0);
        try {
            unusedPort = socket.getLocalPort();
        } finally {
            socket.close();
        }

        HttpChartRenderer renderer = new HttpChartRenderer(
                URI.create("http://127.0.0.1:" + unusedPort + "/render"),
                new io.github.echartsitext.json.JacksonEchartsOptionWriter(),
                Duration.ofMillis(500)
        );

        ChartRenderException exception = assertThrows(ChartRenderException.class,
                () -> renderer.render(sampleChart(), ChartFormat.SVG));

        assertTrue(EnumSet.of(RenderFailureKind.CONNECTION_FAILED, RenderFailureKind.TIMEOUT)
                .contains(exception.getKind()));
        assertEquals(null, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("chart renderer"));
    }

    @Test
    void shouldRejectEmptySuccessfulBody() {
        startServer(exchange -> writeResponse(exchange, 200, "image/svg+xml", new byte[0]));

        HttpChartRenderer renderer = new HttpChartRenderer(renderEndpoint(), new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(2));

        ChartRenderException exception = assertThrows(ChartRenderException.class,
                () -> renderer.render(sampleChart(), ChartFormat.SVG));

        assertEquals(RenderFailureKind.INVALID_RESPONSE, exception.getKind());
        assertTrue(exception.getMessage().contains("empty response body"));
    }

    @Test
    void shouldRejectInvalidRendererArgumentsEarly() {
        assertThrows(NullPointerException.class,
                () -> new HttpChartRenderer(null, new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new HttpChartRenderer(URI.create("/render"), new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new HttpChartRenderer(URI.create("ftp://example.com/render"), new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ofSeconds(1)));
        assertThrows(NullPointerException.class,
                () -> new HttpChartRenderer(URI.create("http://127.0.0.1:8080/render"), null, Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new HttpChartRenderer(URI.create("http://127.0.0.1:8080/render"),
                        new io.github.echartsitext.json.JacksonEchartsOptionWriter(), Duration.ZERO));
    }

    @Test
    void shouldRejectNullRenderInputs() {
        HttpChartRenderer renderer = new HttpChartRenderer(
                URI.create("http://127.0.0.1:8080/render"),
                new io.github.echartsitext.json.JacksonEchartsOptionWriter(),
                Duration.ofSeconds(1)
        );

        assertThrows(NullPointerException.class, () -> renderer.render(null, ChartFormat.SVG));
        assertThrows(NullPointerException.class, () -> renderer.render(sampleChart(), null));
    }

    private void startServer(HttpHandler handler) {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/render", handler);
            server.start();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start test server", e);
        }
    }

    private URI renderEndpoint() {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/render");
    }

    private ChartSpec sampleChart() {
        return Charts.line()
                .size(320, 160)
                .title("Diagnostic Test")
                .series("A", Arrays.asList(
                        new ChartPoint(0d, 0d),
                        new ChartPoint(1d, 1d)
                ))
                .build();
    }

    private static void writeResponse(HttpExchange exchange, int statusCode, String contentType, byte[] body)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, body.length);
        OutputStream outputStream = exchange.getResponseBody();
        try {
            outputStream.write(body);
        } finally {
            outputStream.close();
            exchange.close();
        }
    }
}
