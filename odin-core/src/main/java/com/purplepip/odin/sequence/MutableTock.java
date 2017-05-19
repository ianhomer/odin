package com.purplepip.odin.sequence;

/**
 * Mutable tock.
 */
public class MutableTock implements Tock {
  private long count;
  private Tick tick;

  public MutableTock(Tock tock) {
    this.tick = tock.getTick();
    this.count = tock.getCount();
  }

  public MutableTock(Tick tick, long count) {
    this.tick = tick;
    this.count = count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public void increment() {
    count++;
  }

  public void increment(long increment) {
    count = count + increment;
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
