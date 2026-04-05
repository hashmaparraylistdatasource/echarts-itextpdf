package io.github.echartsitext.pdf;

/**
 * Controls how chart content should scale inside the available PDF content box.
 */
public enum FigureLayoutMode {
    /**
     * Keep the original chart size when there is enough room and only shrink when necessary.
     */
    SHRINK_TO_FIT,
    /**
     * Prefer filling the available width and only fall back to height constraints when required.
     */
    FIT_WIDTH
}
