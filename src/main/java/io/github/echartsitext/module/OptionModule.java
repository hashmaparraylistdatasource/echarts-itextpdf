package io.github.echartsitext.module;

/**
 * Extension point for cross-cutting ECharts features that do not belong in the core chart AST.
 */
public interface OptionModule {
    /**
     * Applies this module to the intermediate option tree after the base chart structure is written.
     */
    void apply(OptionTarget target, ModuleContext context);
}
