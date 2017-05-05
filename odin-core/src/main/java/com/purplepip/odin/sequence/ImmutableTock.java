package com.purplepip.odin.sequence;

/**
 * Immutable tock.
 */
public class ImmutableTock implements Tock {
  private long count;
  private Tick tick;

  public ImmutableTock(Tick tick, long count) {
    this.count = count;
    this.tick = tick;
  }

  public Tick getTick() {
    return tick;
  }

  public long getCount() {
    return count;
  }
}
