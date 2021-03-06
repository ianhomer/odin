/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.creation.track;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.plugin.PluggableAspect;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.SwallowedEvent;
import com.purplepip.odin.roll.Roll;
import com.purplepip.odin.roll.TickConvertedRoll;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Track in the sequencer.
 */
@Slf4j
@ToString(of = "sequenceRoll")
public class SequenceRollTrack implements SequenceTrack, PluggableAspect<SequenceConfiguration> {
  private final Set<Conductor> conductors = new HashSet<>();
  private final Roll roll;
  private SequenceRoll sequenceRoll;
  private final TickConverter tickConverter;

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param measureProvider measure provider
   * @param flowFactory flow factory
   */
  public SequenceRollTrack(SequenceConfiguration sequenceConfiguration, BeatClock clock,
                           MeasureProvider measureProvider,
                           FlowFactory flowFactory, ActionFactory actionFactory) {
    this(clock,
        new MutableSequenceRoll(sequenceConfiguration.copy(),
            clock, flowFactory, actionFactory, measureProvider));
  }

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param sequenceRoll sequence roll to base this track on
   */
  private SequenceRollTrack(BeatClock clock, SequenceRoll sequenceRoll) {
    this.sequenceRoll = sequenceRoll;
    this.tickConverter = new DefaultTickConverter(clock,
        this.sequenceRoll.getTick(), () -> Ticks.MICROSECOND,
        sequenceRoll.getOffsetProperty(), true
    );
    roll = new TickConvertedRoll(sequenceRoll, tickConverter);
  }

  @Override
  public long getId() {
    return getSequence().getId();
  }

  @Override
  public String getName() {
    return getSequence().getName();
  }

  @Override
  public int getChannel() {
    return getSequence().getChannel();
  }

  @Override
  public Event peek() {
    return filter(roll.peek());
  }

  @Override
  public Event pop() {
    return filter(roll.pop());
  }

  @Override
  public Tick getTick() {
    return sequenceRoll.getTick().get();
  }

  /**
   * Get the root sequence runtime, before post-processing.
   *
   * @return root sequence runtime.
   */
  public SequenceRoll getSequenceRoll() {
    return sequenceRoll;
  }

  @Override
  public SequenceConfiguration getSequence() {
    return sequenceRoll.getSequence();
  }

  @Override
  public SequenceConfiguration getConfiguration() {
    return sequenceRoll.getSequence();
  }

  public TickConverter getTickConverter() {
    return tickConverter;
  }

  public void unbindConductors() {
    LOG.debug("Unbinding conductors from {}", this);
    conductors.clear();
  }

  public void bindConductor(Conductor conductor) {
    LOG.debug("Binding conductor {} to {}", conductor, this);
    conductors.add(conductor);
  }

  @Override
  public void setConfiguration(SequenceConfiguration sequence) {
    getSequenceRoll().setSequence(sequence.copy());
  }

  @Override
  public boolean isVoid() {
    return sequenceRoll.isEmpty();
  }

  @Override
  public Map<String, Action> getTriggers() {
    return getSequenceRoll().getTriggers();
  }

  @Override
  public boolean isEnabled() {
    return getSequenceRoll().isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {
    getSequenceRoll().setEnabled(enabled);
  }

  @Override
  public void setProperty(String name, String value) {
    getSequenceRoll().setProperty(name, value);
  }

  @Override
  public String getProperty(String name) {
    return getSequenceRoll().getProperty(name);
  }

  @Override
  public void start() {
    getSequenceRoll().start();
  }

  @Override
  public void stop() {
    getSequenceRoll().stop();
  }

  @Override
  public void initialise() {
    getSequenceRoll().initialise();
  }

  private Event filter(Event event) {
    if (event == null) {
      return null;
    }
    boolean active = false;
    for (Conductor conductor : conductors) {
      if (conductor.isActive(event.getTime().floor())) {
        active = true;
      }
    }
    if (active) {
      return event;
    }
    return new SwallowedEvent(event.getTime());
  }
}
