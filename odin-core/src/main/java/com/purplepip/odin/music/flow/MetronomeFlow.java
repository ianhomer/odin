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

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.extern.slf4j.Slf4j;

/**
 * Metronome flow.
 */
@Slf4j
public class MetronomeFlow extends AbstractFlow<Metronome, Note> {
  @Override
  public Event<Note> getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MovableTock mutableTock = new MovableTock(tock);
    mutableTock.increment(2);
    Note note;
    if ((long) getMeasureProvider().getCount(mutableTock.getCount()) == 0) {
      note = getSequence().getNoteBarStart();
    } else {
      note = getSequence().getNoteBarMid();
    }
    LOG.trace("Creating metronome note {} at {}", note, mutableTock);
    return new DefaultEvent<>(note, mutableTock.getCount());
  }
}
