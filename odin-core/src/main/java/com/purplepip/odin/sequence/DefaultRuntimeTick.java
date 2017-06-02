package com.purplepip.odin.sequence;

/**
 * Default runtime tick.
 */
public class DefaultRuntimeTick implements RuntimeTick {
  private Tick underlyingTick;
  private double factor;
  private int factorAsInt;

  /**
   * Create a default runtime tick.
   *
   * @param tick underlying tick to base this runtime tick off
   */
  public DefaultRuntimeTick(Tick tick) {
    underlyingTick = tick;
    this.factor = (double) tick.getNumerator() / (double) tick.getDenominator();
    this.factorAsInt = tick.getNumerator() / tick.getDenominator();
  }

  @Override
  public TimeUnit getTimeUnit() {
    return underlyingTick.getTimeUnit();
  }

  @Override
  public int getNumerator() {
    return underlyingTick.getNumerator();
  }

  @Override
  public int getDenominator() {
    return underlyingTick.getDenominator();
  }

  @Override
  public double getFactor() {
    return factor;
  }

  @Override
  public int getFactorAsInt() {
    return factorAsInt;
  }

  @Override public String toString() {
    return DefaultRuntimeTick.class.getSimpleName()
        + "(" + this.getNumerator() + "/" + this.getDenominator() + " " + this.getTimeUnit() + ")";
  }
}
