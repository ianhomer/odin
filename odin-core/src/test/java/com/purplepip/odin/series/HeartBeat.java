package com.purplepip.odin.series;

import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.SequenceRuntime;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;

/**
 * SequenceRuntime of heartbeats every second.
 */
public class HeartBeat implements SequenceRuntime<Boolean> {
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
    return Ticks.BEAT;
  }

  private Event<Boolean> getNext() {
    return new DefaultEvent<>(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
  }
}
