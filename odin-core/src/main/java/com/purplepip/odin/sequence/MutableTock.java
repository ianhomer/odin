package com.purplepip.odin.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mutable tock.
 */
public class MutableTock extends Tock {
  private static final Logger LOG = LoggerFactory.getLogger(MutableTock.class);

  public MutableTock(Tick tick, long count) {
    super(tick, count);
  }

  public void increment(int increment) {
    count = count + increment;
    LOG.trace("Tock count {}", count);
  }

  public void increment() {
    count++;
    LOG.trace("Tock count {}", count);
  }
}
