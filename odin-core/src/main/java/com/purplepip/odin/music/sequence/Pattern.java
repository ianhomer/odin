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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Sequences;
import com.purplepip.odin.sequence.SpecialisedSequence;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.Loop;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of pattern.
 */
@ToString(callSuper = true)
@Slf4j
public class Pattern extends GenericSequence implements SpecialisedSequence {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bits;
  private Note note = Notes.newDefault();

  public Pattern() {
    super();
  }

  public Pattern(long id) {
    super(id);
  }

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    Real nextTock = loop.getPosition().getLimit().plus(Wholes.ONE);
    long countInMeasure = context.getMeasureProvider()
        .getCount(nextTock).floor();
    if (getBits() == -1 || ((getBits() >> countInMeasure) & 1) == 1)  {
      return new DefaultEvent<>(getNote(), nextTock);
    }
    return null;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Sequence copy() {
    Pattern copy = new Pattern(this.getId());
    Sequences.copyCoreValues(this, copy);

    copy.setBits(this.getBits());
    copy.setNote(this.getNote());
    return copy;
  }

  public void setBits(int bits) {
    this.bits = bits;
  }

  public int getBits() {
    return bits;
  }

  /**
   * Set note.
   *
   * @param note note
   */
  public void setNote(Note note) {
    if (note == null) {
      LOG.warn("Why has note been set to null?  It will be set to the default note {}",
          Notes.newDefault());
      this.note = Notes.newDefault();
    } else {
      this.note = note;
    }
  }

  public Note getNote() {
    return note;
  }

  @Override
  public boolean isEmpty() {
    return bits == 0 || super.isEmpty();
  }
}
