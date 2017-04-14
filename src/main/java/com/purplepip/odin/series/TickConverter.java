package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class TickConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TickConverter.class);

    private static final float SECONDS_PER_MINUTE = 60.0f;

    private long offset;
    private double secondsPerBeat;
    private Tick outputTick;

    public TickConverter(Tick outputTick) {
        this(outputTick, 0, 0);
    }

    public TickConverter(Tick outputTick, long offset, long bpm) {
        this.outputTick = outputTick;
        this.offset = offset;
        this.secondsPerBeat = SECONDS_PER_MINUTE / bpm;
    }

    public Tick getOutputTick() {
        return outputTick;
    }

    public long convert(Tick inputTick, long time) {
        LOG.trace("Converting {} from {} to {}", time, inputTick, outputTick);
        return offset + convertWithoutOffset(inputTick, time);
    }

    private long convertWithoutOffset(Tick inputTick, long time) {
        return convertTimeUnit(inputTick, time * inputTick.getNumerator() * outputTick.getDenominator() /
                (inputTick.getDenominator() * outputTick.getNumerator()));
    }

    private long convertTimeUnit(Tick inputTick, long time) {
        switch (inputTick.getTimeUnit()) {
            case BEAT:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return time;
                    case SECOND:
                        return (long) (time * secondsPerBeat);
                }
            case SECOND:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return (long) (time / secondsPerBeat);
                    case SECOND:
                        return time;
                }
        }
        throw new RuntimeException("Unexpected time unit " + inputTick.getTimeUnit() + ":" +
            outputTick.getTimeUnit());
    }
}
