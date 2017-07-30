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

package com.purplepip.odin.music.flow;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.tick.Tock;

/**
 * Simple, but noticeable flow if there has been an error in creating a flow.
 */
public class FailOverFlow extends AbstractFlow<Sequence, Note>  {
  @Override
  public Event<Note> getNextEvent(Tock tock) {
    Note note = new DefaultNote(100, 70,2);
    return new DefaultEvent<>(note, tock.getCount() + 1);
  }
}
