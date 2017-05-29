package com.purplepip.odin.sequence;

/**
 * This class consists exclusively of static properties that utilise the Tick model.
 */
public final class Ticks {
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

  private Ticks() {
  }
}
