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

import static com.purplepip.odin.music.notes.Notes.newDefault;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.Sequences;
import com.purplepip.odin.sequence.SpecialisedSequence;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.flow.Loop;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the Metronome.
 *
 */
/*
 * TODO : When persistable sequences are all PersistableSequences and we don't store specific
 * types with
 * specific entities, then we probably can remove the Metronome interface, rename Metronome
 * to Metronome, and move specific (Metronome) flow logic into the Metronome class, and remove
 * from flow definition.  This
 * can also be done with notation and pattern.  This will simplify the work required to add another
 * flow to 1) create flow logic class and 2) create domain class to store configuration.  If
 * desired we could even store the domain class as an inner class of the logic class to keep
 * it all as one.  This will make implementing plugins a lot easier in the future :)
 */
@ToString(callSuper = true)
@Slf4j
@FlowDefinition(name = "metronome")
public class Metronome extends GenericSequence implements SpecialisedSequence {
  private Note noteBarStart = newDefault();
  private Note noteBarMid = newDefault();

  public Metronome() {
    super();
  }

  public Metronome(long id) {
    super(id);
  }

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    Note note;
    Real nextTock = loop.getPosition().getLimit().plus(Wholes.ONE);
    if (nextTock.modulo(Wholes.TWO).equals(Wholes.ZERO)) {
      if (context.getMeasureProvider().getCount(nextTock).floor() == 0) {
        note = getNoteBarStart();
      } else {
        note = getNoteBarMid();
      }
      LOG.trace("Creating metronome note {} at {}", note, loop);
      return new DefaultEvent<>(note, nextTock);
    }
    return null;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public SequenceConfiguration copy() {
    Metronome copy = new Metronome(this.getId());
    Sequences.copyCoreValues(this, copy);

    copy.setNoteBarMid(this.getNoteBarMid());
    copy.setNoteBarStart(this.getNoteBarStart());
    return copy;
  }

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  /**
   * Set the note for the start of the bar.
   *
   * @param note note
   */
  public void setNoteBarStart(Note note) {
    if (note == null) {
      LOG.warn("Why has note bar start been set to null?  It will be set to the default note {}",
          newDefault());
      noteBarStart = newDefault();
    } else {
      noteBarStart = note;
    }
  }

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  public Note getNoteBarMid() {
    return noteBarMid;
  }

  /**
   * Set note for the middle of the bar.
   *
   * @param note note
   */
  public void setNoteBarMid(Note note) {
    if (note == null) {
      LOG.warn("Why has note bar mid been set to null?  It will be set to the default note {}",
          newDefault());
      noteBarMid = newDefault();
    } else {
      noteBarMid = note;
    }
  }
}
