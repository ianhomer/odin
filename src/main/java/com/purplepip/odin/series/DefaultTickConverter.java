package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time unit converter.
 */
public class DefaultTickConverter extends AbstractTickConverter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTickConverter.class);

    private Clock clock;

    public DefaultTickConverter(Clock clock) {
        this(clock, Tick.BEAT);
    }

    public DefaultTickConverter(Clock clock, Tick inputTick) {
        this(clock, inputTick, Tick.MICROSECOND);
    }

    public DefaultTickConverter(Clock clock, Tick inputTick, Tick outputTick) {
        this(clock, inputTick, outputTick, 0);
    }

    public DefaultTickConverter(Clock clock, Tick inputTick, Tick outputTick, long inputOffset) {
        if (clock == null) {
            throw new RuntimeException("Clock must not be null");
        }
        this.clock = clock;
        setInputTick(inputTick);
        setOutputTick(outputTick);
        setInputOffset(inputOffset);
    }

    @Override
    protected long convertTimeUnit(long time) {
        switch (getInputTick().getTimeUnit()) {
            case BEAT:
                switch (getOutputTick().getTimeUnit()) {
                    case BEAT:
                        return scaleTime(time);
                    case MICROSECOND:
                        return clock.getMicroSeconds(getInputTick().getFactor() * time) / getOutputTick().getFactorAsInt();
                }
            case MICROSECOND:
                switch (getOutputTick().getTimeUnit()) {
                    case BEAT:
                        return (long) (clock.getBeat(getInputTick().getFactorAsInt() * time) / getOutputTick().getFactor());
                    case MICROSECOND:
                        return scaleTime(time);
                }
        }
        throw new RuntimeException("Unexpected time unit " + getInputTick().getTimeUnit() + ":" +
            getOutputTick().getTimeUnit());
    }
}
