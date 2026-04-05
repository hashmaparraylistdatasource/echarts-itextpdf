package io.github.echartsitext.render;

import io.github.echartsitext.spec.ChartFormat;

import java.net.URI;
import java.time.Duration;

/**
 * Rich runtime exception for render failures.
 * It keeps endpoint, failure kind, and response details so production callers and issue reports
 * can diagnose renderer problems without re-running under a debugger.
 */
public final class ChartRenderException extends IllegalStateException {
    private static final int MAX_RESPONSE_BODY_LENGTH = 500;

    private final RenderFailureKind kind;
    private final URI endpoint;
    private final Integer statusCode;
    private final ChartFormat format;
    private final int width;
    private final int height;
    private final String responseBody;
    private final Long timeoutMillis;

    private ChartRenderException(String message, Throwable cause, RenderFailureKind kind, URI endpoint,
                                 Integer statusCode, ChartFormat format, int width, int height,
                                 String responseBody, Long timeoutMillis) {
        super(message, cause);
        this.kind = kind;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.format = format;
        this.width = width;
        this.height = height;
        this.responseBody = summarize(responseBody);
        this.timeoutMillis = timeoutMillis;
    }

    public static ChartRenderException connectionFailed(URI endpoint, ChartFormat format,
                                                        int width, int height, Throwable cause) {
        return new ChartRenderException(
                "Failed to reach chart renderer at " + endpoint
                        + " while requesting " + format.extension() + " output for "
                        + width + "x" + height + " chart. Ensure the renderer is running and reachable.",
                cause,
                RenderFailureKind.CONNECTION_FAILED,
                endpoint,
                null,
                format,
                width,
                height,
                null,
                null
        );
    }

    public static ChartRenderException timeout(URI endpoint, ChartFormat format, int width, int height,
                                               Duration timeout, Throwable cause) {
        long timeoutMillis = timeout == null ? -1L : timeout.toMillis();
        return new ChartRenderException(
                "Timed out after " + timeoutMillis + " ms while waiting for chart renderer at " + endpoint
                        + " to produce " + format.extension() + " output for "
                        + width + "x" + height + " chart.",
                cause,
                RenderFailureKind.TIMEOUT,
                endpoint,
                null,
                format,
                width,
                height,
                null,
                timeoutMillis >= 0L ? Long.valueOf(timeoutMillis) : null
        );
    }

    public static ChartRenderException serviceError(URI endpoint, Integer statusCode, ChartFormat format,
                                                    int width, int height, String responseBody) {
        StringBuilder message = new StringBuilder();
        message.append("Chart renderer at ").append(endpoint)
                .append(" returned HTTP ").append(statusCode)
                .append(" while rendering ").append(format.extension())
                .append(" output for ").append(width).append("x").append(height).append(" chart.");
        String summarized = summarize(responseBody);
        if (summarized != null && summarized.length() > 0) {
            message.append(" Response: ").append(summarized);
        }
        return new ChartRenderException(
                message.toString(),
                null,
                RenderFailureKind.SERVICE_ERROR,
                endpoint,
                statusCode,
                format,
                width,
                height,
                responseBody,
                null
        );
    }

    public static ChartRenderException invalidResponse(URI endpoint, ChartFormat format,
                                                       int width, int height, String details) {
        return new ChartRenderException(
                "Chart renderer at " + endpoint + " returned an invalid response while rendering "
                        + format.extension() + " output for " + width + "x" + height + " chart. "
                        + details,
                null,
                RenderFailureKind.INVALID_RESPONSE,
                endpoint,
                null,
                format,
                width,
                height,
                details,
                null
        );
    }

    public RenderFailureKind getKind() {
        return kind;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public ChartFormat getFormat() {
        return format;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    private static String summarize(String body) {
        if (body == null) {
            return null;
        }
        String normalized = body.replace('\r', ' ').replace('\n', ' ').trim();
        if (normalized.length() <= MAX_RESPONSE_BODY_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_RESPONSE_BODY_LENGTH) + "...";
    }
}
