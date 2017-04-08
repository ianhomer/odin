package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class TimeUnitConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUnitConverter.class);

    private static final int MILLIS_PER_MICRO = 1000;
    private long offset;
    private double millisecondsPerBeat;
    private TimeUnit outputTimeUnits;

    public TimeUnitConverter(TimeUnit outputTimeUnits, long offset, long bpm) {
        this.outputTimeUnits = outputTimeUnits;
        this.offset = offset;
        this.millisecondsPerBeat = 60000.0 / bpm;
    }

    public TimeUnit getOutputTimeUnit() {
        return outputTimeUnits;
    }

    public long convert(TimeUnit inputTimeUnits, long time) {
        LOG.debug("Converting {} from {} to {}", time, inputTimeUnits, outputTimeUnits);
        switch (inputTimeUnits) {
            case BEAT:
                switch (outputTimeUnits) {
                    case BEAT:
                        return time;
                    case MILLISECOND:
                        return offset + (long) ((double) time * millisecondsPerBeat);
                    case MICROSECOND:
                        return offset + (long) ((double) time * millisecondsPerBeat * MILLIS_PER_MICRO);
                }
            case MILLISECOND:
                switch (outputTimeUnits) {
                    case BEAT:
                        return (long) ((double) (time - offset) / millisecondsPerBeat);
                    case MILLISECOND:
                        return time;
                    case MICROSECOND:
                        return time * MILLIS_PER_MICRO;
                }
            case MICROSECOND:
                switch (outputTimeUnits) {
                    case BEAT:
                        return (long) ((double) (time - offset) / (millisecondsPerBeat * 1000));
                    case MILLISECOND:
                        return time / 1000;
                    case MICROSECOND:
                        return time;
                }
        }
        throw new RuntimeException("Unexpected time unit " + inputTimeUnits + ":" + outputTimeUnits);
    }
}
