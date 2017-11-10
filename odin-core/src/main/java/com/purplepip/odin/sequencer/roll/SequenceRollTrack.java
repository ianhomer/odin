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

package com.purplepip.odin.sequencer.roll;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.sequence.SequenceFactory;
import com.purplepip.odin.creation.track.SequenceTrack;
import com.purplepip.odin.creation.triggers.Action;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.SwallowedEvent;
import com.purplepip.odin.music.notes.Note;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Track in the sequencer.
 */
@Slf4j
public class SequenceRollTrack implements SequenceTrack {
  private Set<Conductor> conductors = new HashSet<>();
  private Roll<Note> roll;
  private SequenceRoll<Note> sequenceRoll;
  private TickConverter tickConverter;

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param measureProvider measure provider
   * @param sequenceFactory sequence factory
   */
  public SequenceRollTrack(BeatClock clock,
                    MeasureProvider measureProvider,
                    SequenceFactory<Note> sequenceFactory) {
    this(clock,
        new MutableSequenceRoll<>(clock, sequenceFactory, measureProvider));
  }

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param sequenceRoll sequence roll to base this track on
   */
  private SequenceRollTrack(BeatClock clock, SequenceRoll<Note> sequenceRoll) {
    this.sequenceRoll = sequenceRoll;
    this.tickConverter = new DefaultTickConverter(clock,
        this.sequenceRoll.getTick(), () -> Ticks.MICROSECOND,
        sequenceRoll.getOffsetProperty(), false
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
  public Event<Note> peek() {
    return filter(roll.peek());
  }

  @Override
  public Event<Note> pop() {
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
  public SequenceRoll<Note> getSequenceRoll() {
    return sequenceRoll;
  }

  @Override
  public SequenceConfiguration getSequence() {
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

  public void setCopyOfSequence(SequenceConfiguration sequence) {
    getSequenceRoll().setSequence(sequence.copy());
  }

  @Override
  public boolean isEmpty() {
    return sequenceRoll.isEmpty();
  }

  @Override
  public Map<String, Action> getTriggers() {
    return getSequence().getTriggers();
  }

  @Override
  public boolean isEnabled() {
    return getSequenceRoll().isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {
    getSequenceRoll().setEnabled(enabled);
  }

  private Event<Note> filter(Event<Note> event) {
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
    LOG.debug("Event filtered out through conductors : {}", conductors);
    return new SwallowedEvent<>(event.getTime());
  }
}
