package com.purplepip.odin.sequence;

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
    if (getOutputTick().getTimeUnit() == TimeUnit.BEAT) {
      return scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }

  @Override
  protected long getTimeUnitAsMicrosecond(long time) {
    if (getOutputTick().getTimeUnit() == TimeUnit.MICROSECOND) {
      return scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }
}
