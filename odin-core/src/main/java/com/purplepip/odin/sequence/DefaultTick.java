package com.purplepip.odin.sequence;

/**
 * Default tick implementation.
 */
public class DefaultTick implements Tick {
  private TimeUnit timeUnit;
  private int numerator;
  private int denominator;
  private double factor;
  private int factorAsInt;

  public DefaultTick(TimeUnit timeUnit) {
    this(timeUnit, 1);
  }

  public DefaultTick(TimeUnit timeUnit, int numerator) {
    this(timeUnit, numerator, 1);
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
    this.factor = (double) numerator / (double) denominator;
    this.factorAsInt = numerator / denominator;
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

  @Override
  public double getFactor() {
    return factor;
  }

  @Override
  public int getFactorAsInt() {
    return factorAsInt;
  }
}
