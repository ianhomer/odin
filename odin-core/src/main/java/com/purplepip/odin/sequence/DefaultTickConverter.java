package com.purplepip.odin.sequence;

import com.purplepip.odin.common.OdinRuntimeException;

/**
 * Time unit converter.
 */
public class DefaultTickConverter extends AbstractTickConverter {
  private Clock clock;

  /**
   * Create a tick converter.
   *
   * @param clock clock
   * @param inputTick input tick
   * @param outputTick output tick
   * @param inputOffset input offset
   */
  public DefaultTickConverter(Clock clock, RuntimeTick inputTick, RuntimeTick outputTick,
                              long inputOffset) {
    if (clock == null) {
      throw new OdinRuntimeException("Clock must not be null");
    }
    this.clock = clock;
    setInputTick(inputTick);
    setOutputTick(outputTick);
    setInputOffset(inputOffset);
  }

  @Override
  protected long getTimeUnitAsBeat(long time) {
    switch (getOutputTick().getTimeUnit()) {
      case BEAT:
        return scaleTime(time);
      case MICROSECOND:
        return clock.getMicroSeconds(getInputTick().getFactor() * time)
            / getOutputTick().getFactorAsInt();
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  @Override
  protected long getTimeUnitAsMicrosecond(long time) {
    switch (getOutputTick().getTimeUnit()) {
      case BEAT:
        return (long) (clock.getBeat(getInputTick().getFactorAsInt() * time)
            / getOutputTick().getFactor());
      case MICROSECOND:
        return scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }
}
