package com.purplepip.odin.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mutable tock.
 */
public class MutableTock implements Tock {
  private static final Logger LOG = LoggerFactory.getLogger(MutableTock.class);
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

  public Tick getTick() {
    return tick;
  }

  public long getCount() {
    return count;
  }
}
