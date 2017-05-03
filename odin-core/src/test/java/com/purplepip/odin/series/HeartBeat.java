package com.purplepip.odin.series;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;

/**
 * Sequence of heartbeats every second.
 */
public class HeartBeat implements Sequence<Boolean> {
  @Override
  public Event<Boolean> peek() {
    return getNext();
  }

  @Override
  public Event<Boolean> pop() {
    return getNext();
  }

  @Override
  public Tick getTick() {
    return Tick.BEAT;
  }

  private Event<Boolean> getNext() {
    return new DefaultEvent<>(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
  }
}
