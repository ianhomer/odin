package com.purplepip.odin.sequence;

/**
 * Runtime tick with extra methods used by runtime.
 */
public interface RuntimeTick extends Tick {
  double getFactor();

  int getFactorAsInt();
}
