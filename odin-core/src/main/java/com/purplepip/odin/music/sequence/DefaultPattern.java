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

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.sequence.DefaultSequence;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of pattern.
 */
@ToString(callSuper = true)
@Slf4j
public class DefaultPattern extends DefaultSequence implements Pattern {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bits;
  private Note note = Notes.DEFAULT;

  public DefaultPattern() {
    super();
  }

  public DefaultPattern(long id) {
    super(id);
  }

  @Override
  public void setBits(int bits) {
    this.bits = bits;
  }

  @Override
  public int getBits() {
    return bits;
  }

  @Override
  public void setNote(Note note) {
    if (note == null) {
      LOG.warn("Why has note been set to null?  It will be set to the default note {}",
          Notes.DEFAULT);
      this.note = Notes.DEFAULT;
    } else {
      this.note = note;
    }
  }

  @Override
  public Note getNote() {
    return note;
  }
}
