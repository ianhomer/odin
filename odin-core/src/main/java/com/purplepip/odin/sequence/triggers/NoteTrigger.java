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

package com.purplepip.odin.sequence.triggers;

import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequencer.Operation;
import lombok.ToString;

@ToString(exclude = "project")
public class NoteTrigger extends DefaultTrigger implements MutableTrigger {
  private int note;

  public NoteTrigger() {
  }

  public void setNote(int note) {
    this.note = note;
  }

  public int getNote() {
    return note;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    return (operation instanceof NoteOnOperation)
        && ((NoteOnOperation) operation).getNumber() == note;
  }
}
