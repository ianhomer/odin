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

import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.creation.sequence.SequencePlugin;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.NoteEvent;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the Metronome.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Name("metronome")
public class Metronome extends SequencePlugin {
  private Note noteBarStart = Notes.newNote();
  private Note noteBarMid = Notes.newNote();

  @Override
  public NoteEvent getNextEvent(MeasureContext context, Loop loop) {
    Note note;
    Real nextTock = loop.getAbsolutePosition().plus(Wholes.ONE);
    if (nextTock.modulo(Wholes.TWO).equals(Wholes.ZERO)) {
      if (context.getMeasureProvider().getCount(nextTock).floor() == 0) {
        note = noteBarStart;
      } else {
        note = noteBarMid;
      }
      LOG.trace("Creating metronome note {} at {}", note, loop);
      return new NoteEvent(note, nextTock);
    }
    return null;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Metronome copy() {
    return copy(new Metronome());
  }

  protected Metronome copy(Metronome copy) {
    super.copy(copy);
    copy.noteBarStart = this.noteBarStart;
    copy.noteBarMid = this.noteBarMid;
    return copy;
  }

  @Override
  public Metronome name(String name) {
    super.name(name);
    return this;
  }
}
