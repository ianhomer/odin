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

  @Override
  public Tick getTick() {
    return tick;
  }

  @Override
  public long getCount() {
    return count;
  }
}
