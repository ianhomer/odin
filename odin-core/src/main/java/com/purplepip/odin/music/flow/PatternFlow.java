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
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.measure.MeasureProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern flow.
 */
public class PatternFlow extends AbstractFlow<Pattern, Note> {
  private static final Logger LOG = LoggerFactory.getLogger(PatternFlow.class);

  @Override
  public Event<Note> getNextEvent(Tock tock, MeasureProvider measureProvider) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    Event<Note> nextEvent;
    boolean on = false;
    int maxForwardScan = 2 * measureProvider.getBeats(mutableTock);
    int i = 0;
    while (!on && i < maxForwardScan) {
      mutableTock.increment();
      i++;
      long position = measureProvider.getTickPosition(mutableTock);
      on = getSequence().getBits() == -1 || ((getSequence().getBits() >> position) & 1) == 1;
    }

    if (on) {
      nextEvent = new DefaultEvent<>(getSequence().getNote(), mutableTock.getCount());
    } else {
      LOG.debug("No notes found in the next {} ticks after tock {} for pattern {}",
          maxForwardScan, tock, getSequence().getBits());
      nextEvent = new ScanForwardEvent<>(mutableTock.getCount());
    }
    return nextEvent;
  }

}
