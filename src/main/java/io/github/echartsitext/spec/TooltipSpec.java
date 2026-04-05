package io.github.echartsitext.spec;

import io.github.echartsitext.internal.ValidationSupport;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes tooltip behavior, including axis pointer configuration.
 */
public final class TooltipSpec {
    private final boolean show;
    private final String trigger;
    private final Map<String, Object> axisPointer;
    private final String formatter;
    private final Map<String, Object> extensions;

    TooltipSpec(boolean show, String trigger, Map<String, Object> axisPointer, String formatter, Map<String, Object> extensions) {
        this.show = show;
        this.trigger = ValidationSupport.requireNonBlank(trigger, "trigger");
        this.axisPointer = axisPointer == null
                ? Collections.<String, Object>emptyMap()
                : ValidationSupport.immutableMapCopy(axisPointer, "axisPointer");
        this.formatter = formatter;
        this.extensions = ValidationSupport.immutableMapCopy(extensions, "extensions");
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isShow() {
        return show;
    }

    public String getTrigger() {
        return trigger;
    }

    public Map<String, Object> getAxisPointer() {
        return axisPointer;
    }

    public String getFormatter() {
        return formatter;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Fluent builder for tooltip configuration.
     */
    public static final class Builder {
        private boolean show = true;
        private String trigger = "axis";
        private final Map<String, Object> axisPointer = new LinkedHashMap<String, Object>();
        private String formatter;
        private final Map<String, Object> extensions = new LinkedHashMap<String, Object>();

        public Builder show(boolean show) {
            this.show = show;
            return this;
        }

        public Builder trigger(String trigger) {
            this.trigger = ValidationSupport.requireNonBlank(trigger, "trigger");
            return this;
        }

        public Builder axisPointerType(String type) {
            this.axisPointer.put("type", ValidationSupport.requireNonBlank(type, "type"));
            return this;
        }

        public Builder formatter(String formatter) {
            this.formatter = formatter;
            return this;
        }

        public Builder raw(String key, Object value) {
            this.extensions.put(ValidationSupport.requireNonBlank(key, "key"), value);
            return this;
        }

        public TooltipSpec build() {
            return new TooltipSpec(show, trigger, axisPointer, formatter, extensions);
        }
    }
}
