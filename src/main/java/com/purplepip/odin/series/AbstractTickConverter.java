package com.purplepip.odin.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for tick converters.
 */
public abstract class AbstractTickConverter implements TickConverter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTickConverter.class);

    private long inputOffset;
    private Tick inputTick;
    private Tick outputTick;

    protected void setOutputTick(Tick outputTick) {
        this.outputTick = outputTick;
    }

    protected void setInputTick(Tick inputTick) {
        this.inputTick = inputTick;
    }

    protected void setInputOffset(long inputOffset) {
        this.inputOffset = inputOffset;
    }

    @Override
    public Tick getOutputTick() {
        return outputTick;
    }

    protected Tick getInputTick() {
        return inputTick;
    }

    protected long getInputOffset() {
        return inputOffset;
    }

    @Override
    public long convert(long time) {
        LOG.trace("Converting {} from {} to {}", time, inputTick, outputTick);
        return convertTimeUnit(inputOffset + time);
    }

    protected long scaleTime(long time) {
        return time * inputTick.getNumerator() * outputTick.getDenominator() /
                (inputTick.getDenominator() * outputTick.getNumerator());
    }

    protected abstract long convertTimeUnit(long time);


}
