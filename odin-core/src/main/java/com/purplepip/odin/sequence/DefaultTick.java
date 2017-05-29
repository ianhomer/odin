package com.purplepip.odin.sequence;

/**
 * Default tick implementation.
 */
public class DefaultTick implements Tick {
  private TimeUnit timeUnit;
  private int numerator;
  private int denominator;

  public DefaultTick(TimeUnit timeUnit) {
    this(timeUnit, 1);
  }

  public DefaultTick(TimeUnit timeUnit, int numerator) {
    this(timeUnit, numerator, 1);
  }

  public DefaultTick(Tick tick) {
    this(tick.getTimeUnit(), tick.getNumerator(), tick.getDenominator());
  }

  /**
   * Create a tick.
   *
   * @param timeUnit time unit
   * @param numerator numerator
   * @param denominator denominator
   */
  public DefaultTick(TimeUnit timeUnit, int numerator, int denominator) {
    this.timeUnit = timeUnit;
    this.numerator = numerator;
    this.denominator = denominator;
  }

  @Override
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public int getNumerator() {
    return numerator;
  }

  @Override
  public int getDenominator() {
    return denominator;
  }
}
