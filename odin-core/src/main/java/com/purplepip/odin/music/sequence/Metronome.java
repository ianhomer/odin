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
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;

/**
 * Metronome sequence configuration.
 */
public interface Metronome extends MutableSequence {
  @Override
  default Sequence copy() {
    Metronome copy = new DefaultMetronome(this.getId());
    copy.setNoteBarMid(this.getNoteBarMid());
    copy.setNoteBarStart(this.getNoteBarStart());
    copy.setChannel(this.getChannel());
    copy.setFlowName(this.getFlowName());
    copy.setLength(this.getLength());
    copy.setOffset(this.getOffset());
    copy.setProject(this.getProject());
    copy.setTick(this.getTick());
    copy.getLayers().forEach(this::addLayer);
    return copy;
  }

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  Note getNoteBarStart();

  void setNoteBarStart(Note note);

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  Note getNoteBarMid();

  void setNoteBarMid(Note note);
}
