package com.purplepip.odin.sequence;

/**
 * Length of one unit in the time series.
 */
public interface Tick {
  TimeUnit getTimeUnit();

  int getNumerator();

  int getDenominator();
}
