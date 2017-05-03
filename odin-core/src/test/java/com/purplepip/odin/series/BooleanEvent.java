package com.purplepip.odin.series;

import com.purplepip.odin.sequence.Event;

/**
 * Boolean.
 */
public class BooleanEvent implements Event<Boolean> {
  private long time;

  @Override
  public Boolean getValue() {
    return Boolean.TRUE;
  }

  public BooleanEvent(long time) {
    this.time = time;
  }

  @Override
  public long getTime() {
    return time;
  }
}
