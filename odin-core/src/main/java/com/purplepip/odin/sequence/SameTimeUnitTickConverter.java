package com.purplepip.odin.sequence;

/**
 * Tick converter that is not clock aware and can only convert ticks with the same time unit.
 */
public class SameTimeUnitTickConverter extends AbstractTickConverter {
  public SameTimeUnitTickConverter(Tick inputTick) {
    this(inputTick, Tick.MICROSECOND);
  }

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
  protected long convertTimeUnit(long time) {
    switch (getInputTick().getTimeUnit()) {
      case BEAT:
        switch (getOutputTick().getTimeUnit()) {
          case BEAT:
            return scaleTime(time);
          default:
            return throwUnexpectedTimeUnit();
        }
      case MICROSECOND:
        switch (getOutputTick().getTimeUnit()) {
          case MICROSECOND:
            return scaleTime(time);
          default:
            return throwUnexpectedTimeUnit();

        }
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  private long throwUnexpectedTimeUnit() {
    throw new RuntimeException("Unexpected time unit " + getInputTick().getTimeUnit() + ":"
        + getOutputTick().getTimeUnit());
  }
}
