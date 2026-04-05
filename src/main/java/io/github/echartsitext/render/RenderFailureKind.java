package io.github.echartsitext.render;

/**
 * High-level categories for renderer failures so callers can react without parsing messages.
 */
public enum RenderFailureKind {
    CONNECTION_FAILED,
    TIMEOUT,
    SERVICE_ERROR,
    INVALID_RESPONSE
}
