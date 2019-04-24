package com.purplepip.odin.series;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Bound;
import com.purplepip.odin.properties.runtime.Property;
import com.purplepip.odin.roll.Roll;
import lombok.extern.slf4j.Slf4j;

/**
 * Roll of heartbeats every second.
 */
@Slf4j
public class HeartBeat implements Roll {
  @Override
  public Event peek() {
    return getNext();
  }

  @Override
  public Event pop() {
    return getNext();
  }

  @Override
  public void setTock(Bound tock) {
    // No operation since the heart beat clock is locked into the system clock.
  }

  @Override
  public Property<Tick> getTick() {
    return () -> Ticks.BEAT;
  }

  private Event getNext() {
    return new DefaultEvent(Boolean.TRUE, ((System.currentTimeMillis() + 999) / 1000) * 1000);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public String getName() {
    return "heart-beat";
  }

  @Override
  public void start() {
    LOG.warn("Redundant operation for start on {}", this);
  }

  @Override
  public void stop() {
    LOG.warn("Redundant operation for stop on {}", this);
  }

  @Override
  public void initialise() {
    LOG.warn("Redundant operation for initialise on {}", this);
  }

  @Override
  public void setProperty(String name, String value) {
    throw new OdinRuntimeException("Cannot set property on " + getClass().getName());
  }

  @Override
  public String getProperty(String name) {
    throw new OdinRuntimeException("Cannot get property on " + getClass().getName());
  }
}
