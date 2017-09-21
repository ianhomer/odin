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

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Sequences;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Pattern sequence configuration.
 */
public interface Pattern extends MutableSequence {
  @Override
  default Sequence copy() {
    Pattern copy = new DefaultPattern(this.getId());
    Sequences.copyCoreValues(this, copy);

    copy.setBits(this.getBits());
    copy.setNote(this.getNote());
    return copy;
  }

  void setBits(int bits);

  @Min(0)
  int getBits();

  void setNote(Note note);

  @NotNull
  Note getNote();
}
