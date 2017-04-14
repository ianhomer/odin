package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class TickConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TickConverter.class);

    private long inputOffset;
    /*
     * A non-negative value of the output offset is set when the clock knows for sure what it is.  The clock
     * only knows when the time has passed.
     */
    private long outputOffset = -1;
    private Clock clock;
    private Tick inputTick;
    private Tick outputTick;

    public TickConverter(Clock clock) {
        this(clock, Tick.BEAT);
    }

    public TickConverter(Clock clock, Tick inputTick) {
        this(clock, inputTick, Tick.MICROSECOND);
    }

    public TickConverter(Clock clock, Tick inputTick, Tick outputTick) {
        this(clock, inputTick, outputTick, 0);
    }

    public TickConverter(Clock clock, Tick inputTick, Tick outputTick, long inputOffset) {
        this.clock = clock;
        this.inputTick = inputTick;
        this.outputTick = outputTick;
        this.inputOffset = inputOffset;
    }

    public Tick getOutputTick() {
        return outputTick;
    }

    public long convert(long time) {
        LOG.trace("Converting {} from {} to {}", time, inputTick, outputTick);
        return getOutputOffset() + convertTimeUnit(time);
    }

    private long scaleTime(long time) {
        return time * inputTick.getNumerator() * outputTick.getDenominator() /
                (inputTick.getDenominator() * outputTick.getNumerator());
    }

    private long convertTimeUnit(long time) {
        switch (inputTick.getTimeUnit()) {
            case BEAT:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return scaleTime(time);
                    case SECOND:
                        return (long) (clock.getSeconds(inputTick.getFactor() * time) / outputTick.getFactor());
                }
            case SECOND:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return (long) (clock.getBeat(inputTick.getFactor() * time) / outputTick.getFactor());
                    case SECOND:
                        return scaleTime(time);
                }
        }
        throw new RuntimeException("Unexpected time unit " + inputTick.getTimeUnit() + ":" +
            outputTick.getTimeUnit());
    }

    /*
     * We do not know accurately the output offset until the time has pass since the BPM might change
     * before the series starts.
     * TODO : Handle offsets, code below is WIP
     */
    private long getOutputOffset() {
        if (outputOffset > -1) {
            return outputOffset;
        }
        long outputOffsetEstimate = convertTimeUnit(inputOffset);
        boolean isOutputOffsetAccurate = true;
        /*
        switch (inputTick.getTimeUnit()) {
            case BEAT:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        outputOffsetEstimate = scaleTime(inputOffset);
                        isOutputOffsetAccurate = true;
                        break;
                    case SECOND:
                        return (long) (inputOffset);
                }
            case SECOND:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return (long) (inputOffset);
                    case SECOND:
                        outputOffsetEstimate = scaleTime(inputOffset);
                        isOutputOffsetAccurate = true;
                        break;
                }
        }
        */
        if (isOutputOffsetAccurate) {
            outputOffset = outputOffsetEstimate;
        }
        return outputOffsetEstimate;
    }
}
