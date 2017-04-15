package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class TickConverter {
    private static final Logger LOG = LoggerFactory.getLogger(TickConverter.class);

    private long inputOffset;
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
        if (clock == null) {
            throw new RuntimeException("Clock must not be null");
        }
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
        return convertTimeUnit(inputOffset + time);
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
                    case MICROSECOND:
                        return clock.getMicroSeconds(inputTick.getFactor() * time) / outputTick.getFactorAsInt();
                }
            case MICROSECOND:
                switch (outputTick.getTimeUnit()) {
                    case BEAT:
                        return (long) (clock.getBeat(inputTick.getFactorAsInt() * time) / outputTick.getFactor());
                    case MICROSECOND:
                        return scaleTime(time);
                }
        }
        throw new RuntimeException("Unexpected time unit " + inputTick.getTimeUnit() + ":" +
            outputTick.getTimeUnit());
    }
}
