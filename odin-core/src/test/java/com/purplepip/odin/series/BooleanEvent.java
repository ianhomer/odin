package com.purplepip.odin.series;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;

/**
 * Boolean.
 */
public class BooleanEvent implements Event<Boolean> {
  private Rational time;

  @Override
  public Boolean getValue() {
    return Boolean.TRUE;
  }

  public BooleanEvent(Rational time) {
    this.time = time;
  }

  @Override
  public Rational getTime() {
    return time;
  }
}
