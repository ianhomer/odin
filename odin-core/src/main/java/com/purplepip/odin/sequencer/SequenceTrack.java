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

import com.purplepip.odin.music.Note;
import com.purplepip.odin.properties.Mutable;
import com.purplepip.odin.properties.ObservableProperty;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceRoll;
import com.purplepip.odin.sequence.TickConvertedRoll;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;

/**
 * Track in the sequencer.
 */
public class SequenceTrack implements Track {
  private SequenceRoll<Note> sequenceRoll;
  private TickConverter tickConverter;
  private Roll<Note> roll;
  private Mutable<Tick> tick;

  /**
   * Create new track.
   *
   * @param clock beat clock
   * @param sequenceRoll sequence roll to base this track on
   */
  SequenceTrack(BeatClock clock, SequenceRoll<Note> sequenceRoll, Sequence sequence) {
    this.tick = new ObservableProperty<>(sequence.getTick());
    /*
     * Sequence is set late, so any sequence set here will change.
     *
     * TODO : Improve this logic, I don't like using sequence in this constructor since logically
     * it'll have no impact.  This may be confusing at some point
     */
    sequenceRoll.setSequence(sequence);
    this.sequenceRoll = sequenceRoll;
    this.tickConverter = new DefaultTickConverter(clock,
        tick, () -> Ticks.MICROSECOND,
        sequenceRoll.getOffsetProperty()
    );
    roll = new TickConvertedRoll(sequenceRoll, tickConverter);
  }


  public int getChannel() {
    return getSequence().getChannel();
  }

  /**
   * Get end of pipe line sequence runtime, after any post processing.
   *
   * @return sequence runtime.
   */
  public Roll<Note> getRoll() {
    return roll;
  }

  @Override
  public Tick getTick() {
    return tick.get();
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

  public Mutable<Tick> getMutableTick() {
    return tick;
  }
}
