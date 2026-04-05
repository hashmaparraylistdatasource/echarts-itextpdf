package io.github.echartsitext.render;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.echartsitext.internal.ValidationSupport;
import io.github.echartsitext.json.EchartsOptionWriter;
import io.github.echartsitext.json.JacksonEchartsOptionWriter;
import io.github.echartsitext.spec.ChartFormat;
import io.github.echartsitext.spec.ChartSpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Generic HTTP renderer that posts ECharts option JSON to an external render service.
 */
public final class HttpChartRenderer implements ChartRenderer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final URI endpoint;
    private final EchartsOptionWriter optionWriter;
    private final Duration timeout;

    public HttpChartRenderer(URI endpoint) {
        this(endpoint, new JacksonEchartsOptionWriter(), Duration.ofSeconds(30));
    }

    public HttpChartRenderer(URI endpoint, EchartsOptionWriter optionWriter, Duration timeout) {
        this.endpoint = requireEndpoint(endpoint);
        this.optionWriter = Objects.requireNonNull(optionWriter, "optionWriter");
        this.timeout = requireTimeout(timeout);
    }

    @Override
    public RenderedChart render(ChartSpec spec, ChartFormat format) {
        HttpURLConnection connection = null;
        try {
            Objects.requireNonNull(spec, "spec");
            Objects.requireNonNull(format, "format");
            String payload = OBJECT_MAPPER.writeValueAsString(createPayload(spec, format));
            connection = (HttpURLConnection) endpoint.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout((int) timeout.toMillis());
            connection.setReadTimeout((int) timeout.toMillis());
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "image/svg+xml, image/png, application/json");

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            // Always consume either the normal response or the error stream so callers get useful diagnostics.
            int statusCode = connection.getResponseCode();
            byte[] body = readFully(statusCode >= 200 && statusCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());
            if (statusCode < 200 || statusCode >= 300) {
                String errorBody = new String(body, StandardCharsets.UTF_8);
                throw ChartRenderException.serviceError(endpoint, Integer.valueOf(statusCode), format,
                        spec.getWidth(), spec.getHeight(), errorBody);
            }
            if (body.length == 0) {
                throw ChartRenderException.invalidResponse(endpoint, format, spec.getWidth(), spec.getHeight(),
                        "The renderer returned HTTP " + statusCode + " with an empty response body.");
            }
            return new RenderedChart(format, body, spec.getWidth(), spec.getHeight());
        } catch (SocketTimeoutException e) {
            throw ChartRenderException.timeout(endpoint, format, spec.getWidth(), spec.getHeight(), timeout, e);
        } catch (IOException e) {
            throw ChartRenderException.connectionFailed(endpoint, format, spec.getWidth(), spec.getHeight(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private Map<String, Object> createPayload(ChartSpec spec, ChartFormat format) {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<String, Object>();
        // The payload shape intentionally mirrors the built-in local render service.
        payload.put("option", optionWriter.writeTree(spec));
        payload.put("width", spec.getWidth());
        payload.put("height", spec.getHeight());
        payload.put("type", format.extension());
        payload.put("backgroundColor", spec.getBackgroundColor());
        return payload;
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }
        try (InputStream in = inputStream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    private static URI requireEndpoint(URI endpoint) {
        Objects.requireNonNull(endpoint, "endpoint");
        if (!endpoint.isAbsolute()) {
            throw new IllegalArgumentException("endpoint must be an absolute URI");
        }
        String scheme = endpoint.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("endpoint must use http or https");
        }
        return endpoint;
    }

    private static Duration requireTimeout(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");
        ValidationSupport.requirePositive(timeout.toMillis(), "timeout");
        if (timeout.toMillis() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("timeout must be less than or equal to " + Integer.MAX_VALUE + "ms");
        }
        return timeout;
    }
}
