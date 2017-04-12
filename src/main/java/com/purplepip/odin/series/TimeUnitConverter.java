package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class TimeUnitConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUnitConverter.class);

    private static final int HALF_BEATS_PER_BEAT = 2;

    private static final int MILLIS_PER_SECOND = 1000;
    private static final int MICROS_PER_MILLI = 1000;
    private static final int MICROS_PER_SECOND = 1000000;
    private static final float SECONDS_PER_MINUTE = 60.0f;

    private long offset;
    private double secondsPerBeat;
    private double microSecondsPerBeat;
    private double millisSecondsPerBeat;
    private TimeUnit outputTimeUnits;

    public TimeUnitConverter(TimeUnit outputTimeUnits) {
        this(outputTimeUnits, 0, 0);
    }

    public TimeUnitConverter(TimeUnit outputTimeUnits, long offset, long bpm) {
        this.outputTimeUnits = outputTimeUnits;
        this.offset = offset;
        this.microSecondsPerBeat = SECONDS_PER_MINUTE * MICROS_PER_SECOND / bpm;
        this.millisSecondsPerBeat = SECONDS_PER_MINUTE * MILLIS_PER_SECOND / bpm;
        this.secondsPerBeat = SECONDS_PER_MINUTE / bpm;
    }

    public TimeUnit getOutputTimeUnit() {
        return outputTimeUnits;
    }

    public long convert(TimeUnit inputTimeUnits, long time) {
        LOG.trace("Converting {} from {} to {}", time, inputTimeUnits, outputTimeUnits);
        return offset + convertWithoutOffset(inputTimeUnits, time);
    }

    public long convertWithoutOffset(TimeUnit inputTimeUnits, long time) {
        switch (inputTimeUnits) {
            case BEAT:
                switch (outputTimeUnits) {
                    case BEAT:
                        return time;
                    case HALF_BEAT:
                        return time * HALF_BEATS_PER_BEAT;
                    case SECOND:
                        return (long) (time * secondsPerBeat);
                    case MILLISECOND:
                        return (long) (time * millisSecondsPerBeat);
                    case MICROSECOND:
                        return (long) (time * microSecondsPerBeat);
                }
            case HALF_BEAT:
                switch (outputTimeUnits) {
                    case BEAT:
                        return time / HALF_BEATS_PER_BEAT;
                    case HALF_BEAT:
                        return time;
                    case SECOND:
                        return (long) (time * secondsPerBeat / HALF_BEATS_PER_BEAT);
                    case MILLISECOND:
                        return (long) (time * millisSecondsPerBeat / HALF_BEATS_PER_BEAT);
                    case MICROSECOND:
                        return (long) (time * microSecondsPerBeat / HALF_BEATS_PER_BEAT);
                }
            case MILLISECOND:
                switch (outputTimeUnits) {
                    case BEAT:
                        return (long) (time / millisSecondsPerBeat);
                    case HALF_BEAT:
                        return (long) (time * HALF_BEATS_PER_BEAT / millisSecondsPerBeat);
                    case SECOND:
                        return time / MILLIS_PER_SECOND;
                    case MILLISECOND:
                        return time;
                    case MICROSECOND:
                        return time * MICROS_PER_MILLI;
                }
            case MICROSECOND:
                switch (outputTimeUnits) {
                    case BEAT:
                        return (long) (time / microSecondsPerBeat);
                    case HALF_BEAT:
                        return (long) (time * HALF_BEATS_PER_BEAT / microSecondsPerBeat);
                    case SECOND:
                        return time / MICROS_PER_SECOND;
                    case MILLISECOND:
                        return time / MICROS_PER_MILLI;
                    case MICROSECOND:
                        return time;
                }
        }
        throw new RuntimeException("Unexpected time unit " + inputTimeUnits + ":" + outputTimeUnits);
    }
}
