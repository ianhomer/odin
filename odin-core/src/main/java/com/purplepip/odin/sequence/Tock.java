package com.purplepip.odin.sequence;

/**
 * A positioned tick.
 */
public class Tock {
  protected long count;
  private Tick tick;

  public Tock(Tick tick, long count) {
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
