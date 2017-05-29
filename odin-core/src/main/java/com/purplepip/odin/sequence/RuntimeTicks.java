package com.purplepip.odin.sequence;

/**
 * This class consists exclusively of static properties that utilise the RuntimeTick model.
 */
public final class RuntimeTicks {
  public static final RuntimeTick MICROSECOND = new DefaultRuntimeTick(Ticks.MICROSECOND);
  public static final RuntimeTick BEAT = new DefaultRuntimeTick(Ticks.BEAT);
  public static final RuntimeTick HALF = new DefaultRuntimeTick(Ticks.HALF);
  public static final RuntimeTick MILLISECOND = new DefaultRuntimeTick(Ticks.MILLISECOND);
  public static final RuntimeTick MEASURE = new DefaultRuntimeTick(Ticks.MEASURE);

  private RuntimeTicks() {
  }
}
