package io.github.echartsitext.spec;

/**
 * One OHLC candlestick item in ECharts order: open, close, low, high.
 */
public final class CandlestickValue {
    private final Number open;
    private final Number close;
    private final Number low;
    private final Number high;

    public CandlestickValue(Number open, Number close, Number low, Number high) {
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
    }

    public Number getOpen() {
        return open;
    }

    public Number getClose() {
        return close;
    }

    public Number getLow() {
        return low;
    }

    public Number getHigh() {
        return high;
    }
}
