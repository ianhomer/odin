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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.DefaultSequence;
import lombok.ToString;

/**
 * Default implementation of the Metronome.
 *
 */
/*
 * TODO : When persistable sequences are all PersistableSequences and we don't store specific types with
 * specific entities, then we probably can remove the Metronome interface, rename DefaultMetronome
 * to Metronome, use that as generic type in Metronome flow, and remove from flow definition.  This
 * can also be done with notation and pattern.  This will simply the work required to add another
 * flow to 1) create flow logic class and 2) create domain class to store configuration.  If
 * desired we could even store the domain class as an inner class of the logic class to keep
 * it all as one.  This will make plugability a lot easier in the future :)
 */
@ToString(callSuper = true)
public class DefaultMetronome extends DefaultSequence implements Metronome {
  private Note noteBarStart = new DefaultNote();
  private Note noteBarMid = new DefaultNote();

  public DefaultMetronome() {
    super();
  }

  public DefaultMetronome(long id) {
    super(id);
  }

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  @Override
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  @Override
  public void setNoteBarStart(Note note) {
    noteBarStart = note;
  }

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  @Override
  public Note getNoteBarMid() {
    return noteBarMid;
  }

  @Override
  public void setNoteBarMid(Note note) {
    noteBarMid = note;
  }
}
