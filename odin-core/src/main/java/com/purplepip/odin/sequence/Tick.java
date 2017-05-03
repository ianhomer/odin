package com.purplepip.odin.sequence;

/**
 * Length of one unit in the time series.
 */
public class Tick {
  private TimeUnit timeUnit;
  private int numerator;
  private int denominator;
  private double factor;
  private int factorAsInt;

  public static Tick SECOND = new Tick(TimeUnit.MICROSECOND, 1000000);
  public static Tick MILLISECOND = new Tick(TimeUnit.MICROSECOND, 1000);
  public static Tick MICROSECOND = new Tick(TimeUnit.MICROSECOND);
  public static Tick BEAT = new Tick(TimeUnit.BEAT);
  public static Tick HALF = new Tick(TimeUnit.BEAT, 1, 2);
  public static Tick FOUR_THIRDS = new Tick(TimeUnit.BEAT, 4, 3);
  public static Tick TWO_THIRDS = new Tick(TimeUnit.BEAT, 2, 3);
  public static Tick THIRD = new Tick(TimeUnit.BEAT, 1, 3);
  public static Tick QUARTER = new Tick(TimeUnit.BEAT, 1, 4);
  public static Tick EIGHTH = new Tick(TimeUnit.BEAT, 1, 8);

  public Tick(TimeUnit timeUnit) {
    this(timeUnit, 1);
  }

  public Tick(TimeUnit timeUnit, int numerator) {
    this(timeUnit, numerator, 1);
  }

  /**
   * Create a tick.
   *
   * @param timeUnit time unit
   * @param numerator numerator
   * @param denominator denominator
   */
  public Tick(TimeUnit timeUnit, int numerator, int denominator) {
    this.timeUnit = timeUnit;
    this.numerator = numerator;
    this.denominator = denominator;
    this.factor = (double) numerator / (double) denominator;
    this.factorAsInt = numerator / denominator;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public int getNumerator() {
    return numerator;
  }

  public int getDenominator() {
    return denominator;
  }

  public double getFactor() {
    return factor;
  }

  public int getFactorAsInt() {
    return factorAsInt;
  }
}
