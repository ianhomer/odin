package com.purplepip.odin.series;

import com.purplepip.odin.sequence.AbstractSequenceRuntime;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;

/**
 * SequenceRuntime of heartbeats every second.
 */
public class HeartBeat extends AbstractSequenceRuntime<Boolean> {
  private Sequence sequence = new HeartBeatSequence();

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

  @Override
  public Sequence getSequence() {
    return getSequence();
  }

  private Event<Boolean> getNext() {
    return new DefaultEvent<>(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
  }
}
