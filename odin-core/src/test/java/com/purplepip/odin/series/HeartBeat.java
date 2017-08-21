package com.purplepip.odin.series;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.Property;
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;

/**
 * Roll of heartbeats every second.
 */
public class HeartBeat implements Roll<Boolean> {
  @Override
  public Event<Boolean> peek() {
    return getNext();
  }

  @Override
  public Event<Boolean> pop() {
    return getNext();
  }

  @Override
  public void setTock(Whole tock) {
    // No operation since the heart beat clock is locked into the system clock.
  }

  @Override
  public Property<Tick> getTick() {
    return () -> Ticks.BEAT;
  }

  private Event<Boolean> getNext() {
    return new DefaultEvent<>(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }
}
