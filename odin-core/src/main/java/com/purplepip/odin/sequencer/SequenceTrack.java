/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceRoll;
import com.purplepip.odin.sequence.TickConvertedRoll;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.conductor.Conductor;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.HashSet;
import java.util.Set;

/**
 * Track in the sequencer.
 */
public class SequenceTrack implements Track {
  @Override
  public long getId() {
    return getSequence().getId();
  }

  private SequenceRoll<Note> sequenceRoll;
  private TickConverter tickConverter;
  private Roll<Note> roll;
  private Set<Conductor> conductors = new HashSet<>();

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param sequenceRoll sequence roll to base this track on
   */
  SequenceTrack(BeatClock clock, SequenceRoll<Note> sequenceRoll) {
    this.sequenceRoll = sequenceRoll;
    this.tickConverter = new DefaultTickConverter(clock,
        this.sequenceRoll.getTick(), () -> Ticks.MICROSECOND,
        sequenceRoll.getOffsetProperty(), false
    );
    roll = new TickConvertedRoll(sequenceRoll, tickConverter);
  }

  @Override
  public int getChannel() {
    return getSequence().getChannel();
  }

  @Override
  public Event<Note> peek() {
    return roll.peek();
  }

  @Override
  public Event<Note> pop() {
    return roll.pop();
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

  public Sequence getSequence() {
    return sequenceRoll.getSequence();
  }

  public TickConverter getTickConverter() {
    return tickConverter;
  }

  public void unbindConductors() {
    conductors.clear();
  }

  public void bindConductor(Conductor conductor) {
    conductors.add(conductor);
  }

  public void setCopyOfSequence(Sequence sequence) {
    getSequenceRoll().setSequence(sequence.copy());
  }
}
