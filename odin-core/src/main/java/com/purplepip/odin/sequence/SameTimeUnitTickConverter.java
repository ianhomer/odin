package com.purplepip.odin.sequence;

import com.purplepip.odin.common.OdinRuntimeException;

/**
 * Tick converter that is not clock aware and can only convert ticks with the same time unit.
 */
public class SameTimeUnitTickConverter extends AbstractTickConverter {
  public SameTimeUnitTickConverter(Tick inputTick, Tick outputTick) {
    this(inputTick, outputTick, 0);
  }

  /**
   * Create same time unit tick converter.
   *
   * @param inputTick input tick
   * @param outputTick output tick
   * @param inputOffset input offset
   */
  public SameTimeUnitTickConverter(Tick inputTick, Tick outputTick, long inputOffset) {
    setInputTick(inputTick);
    setOutputTick(outputTick);
    setInputOffset(inputOffset);
  }

  @Override
  protected long getTimeUnitAsBeat(long time) {
    switch (getOutputTick().getTimeUnit()) {
      case BEAT:
        return scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  @Override
  protected long getTimeUnitAsMicrosecond(long time) {
    switch (getOutputTick().getTimeUnit()) {
      case MICROSECOND:
        return scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();

    }
  }
}
