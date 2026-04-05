package io.github.echartsitext.spec;

/**
 * One five-number boxplot sample in ECharts order: min, Q1, median, Q3, max.
 */
public final class BoxplotValue {
    private final Number min;
    private final Number q1;
    private final Number median;
    private final Number q3;
    private final Number max;

    public BoxplotValue(Number min, Number q1, Number median, Number q3, Number max) {
        this.min = min;
        this.q1 = q1;
        this.median = median;
        this.q3 = q3;
        this.max = max;
    }

    public Number getMin() {
        return min;
    }

    public Number getQ1() {
        return q1;
    }

    public Number getMedian() {
        return median;
    }

    public Number getQ3() {
        return q3;
    }

    public Number getMax() {
        return max;
    }
}
