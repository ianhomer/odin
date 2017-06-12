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

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Metronome flow.
 */
public class MetronomeFlow extends AbstractFlow<Metronome, Note> {
  @Override
  public Event<Note> getNextEvent(Tock tock, MeasureProvider measureProvider) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    mutableTock.increment(2);
    Note note;
    if (measureProvider.getTickPositionInThisMeasure(mutableTock) == 0) {
      note = getSequence().getNoteBarStart();
    } else {
      note = getSequence().getNoteMidBar();
    }
    return new DefaultEvent<>(note, mutableTock.getCount());
  }
}
