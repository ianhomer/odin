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
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceRoll;
import com.purplepip.odin.sequence.TickConvertedRoll;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.tick.RuntimeTick;

/**
 * SequenceRuntime driving a sequenced track.
 */
public class Track {
  private SequenceRoll<Note> sequenceRoll;
  private TickConverter tickConverter;
  private Roll<Note> roll;
  private Mutable<RuntimeTick> runtimeTick;

  /**
   * Create new track.
   *
   * @param sequenceRoll sequence roll to base this track on
   * @param tickConverter tick converter for this sequence roll
   */
  public Track(Mutable<RuntimeTick> runtimeTick,
               SequenceRoll<Note> sequenceRoll, TickConverter tickConverter) {
    // TODO : Generate tickConverter in this constructor as opposed to argument in
    roll = new TickConvertedRoll(sequenceRoll, tickConverter);
    this.tickConverter = tickConverter;
    this.sequenceRoll = sequenceRoll;
    this.runtimeTick = runtimeTick;
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

  public Mutable<RuntimeTick> getTick() {
    return runtimeTick;
  }
}
