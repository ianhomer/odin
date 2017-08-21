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
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.Tock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern flow.
 */
@FlowDefinition(name = "Pattern", sequence = DefaultPattern.class)
public class PatternFlow extends AbstractFlow<Pattern, Note> {
  private static final Logger LOG = LoggerFactory.getLogger(PatternFlow.class);

  @Override
  public Event<Note> getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MovableTock mutableTock = new MovableTock(tock);
    Event<Note> nextEvent;
    boolean on = false;
    int i = 0;
    long maxScanForward = getMaxScanForward().floor();
    while (!on && i < maxScanForward) {
      mutableTock.increment();
      i++;
      long countInMeasure = getMeasureProvider()
          .getCount(mutableTock.getPosition()).floor();
      on = getSequence().getBits() == -1 || ((getSequence().getBits() >> countInMeasure) & 1) == 1;
    }

    if (on) {
      nextEvent = new DefaultEvent<>(getSequence().getNote(), mutableTock.getPosition());
    } else {
      LOG.debug("No notes found in the next {} ticks after tock {} for pattern {}",
          maxScanForward, tock, getSequence().getBits());
      nextEvent = new ScanForwardEvent<>(mutableTock.getPosition());
    }
    return nextEvent;
  }

  public boolean isEmpty() {
    return getSequence().getBits() == 0;
  }
}
