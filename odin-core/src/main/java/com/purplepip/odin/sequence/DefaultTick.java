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

  public static final Tick SECOND = new DefaultTick(TimeUnit.MICROSECOND, 1000000);
  public static final Tick MILLISECOND = new DefaultTick(TimeUnit.MICROSECOND, 1000);
  public static final Tick MICROSECOND = new DefaultTick(TimeUnit.MICROSECOND);
  public static final Tick BEAT = new DefaultTick(TimeUnit.BEAT);
  public static final Tick HALF = new DefaultTick(TimeUnit.BEAT, 1, 2);
  public static final Tick FOUR_THIRDS = new DefaultTick(TimeUnit.BEAT, 4, 3);
  public static final Tick TWO_THIRDS = new DefaultTick(TimeUnit.BEAT, 2, 3);
  public static final Tick THIRD = new DefaultTick(TimeUnit.BEAT, 1, 3);
  public static final Tick QUARTER = new DefaultTick(TimeUnit.BEAT, 1, 4);
  public static final Tick EIGHTH = new DefaultTick(TimeUnit.BEAT, 1, 8);

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
