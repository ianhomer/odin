package com.purplepip.odin.series;

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
        }
      case MICROSECOND:
        switch (getOutputTick().getTimeUnit()) {
          case MICROSECOND:
            return scaleTime(time);
        }
    }
    throw new RuntimeException("Unexpected time unit " + getInputTick().getTimeUnit() + ":" +
        getOutputTick().getTimeUnit());
  }
}
