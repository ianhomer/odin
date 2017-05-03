package com.purplepip.odin.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Event.
 */
public class DefaultEvent<A> implements Event<A> {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultEvent.class);
  private A value;
  private long time;

  /**
   * Create a default event.
   *
   * @param value value for the event
   * @param time time of the event
   */
  public DefaultEvent(A value, long time) {
    LOG.trace("Creating new event : {} at time {}", value, time);
    this.value = value;
    this.time = time;
  }

  @Override
  public A getValue() {
    return value;
  }

  @Override
  public long getTime() {
    return time;
  }
}
