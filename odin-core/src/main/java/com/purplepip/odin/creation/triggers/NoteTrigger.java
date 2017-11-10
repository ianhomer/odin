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

package com.purplepip.odin.creation.triggers;

import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Name("note")
public class NoteTrigger extends GenericTrigger implements SpecialisedTrigger {
  private Note note = newNote();

  public NoteTrigger note(Note note) {
    this.note = note;
    return this;
  }

  public NoteTrigger note(int note) {
    this.note = newNote(note);
    return this;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public NoteTrigger copy() {
    return copy(new NoteTrigger(), this);
  }

  protected NoteTrigger copy(NoteTrigger copy, NoteTrigger original) {
    super.copy(copy, original);
    copy.note = original.note;
    return copy;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    return (operation instanceof NoteOnOperation)
        && ((NoteOnOperation) operation).getNumber() == getNote().getNumber();
  }
}
