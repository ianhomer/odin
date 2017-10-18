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

package com.purplepip.odin.music.flow;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.extern.slf4j.Slf4j;

/**
 * Metronome flow.
 */
@Slf4j
@FlowDefinition(name = "metronome", sequence = Metronome.class)
public class MetronomeFlow extends AbstractFlow<Metronome, Note> {
  public MetronomeFlow(Clock clock, MeasureProvider measureProvider) {
    super(clock, measureProvider);
  }

  /**
   * Get event for a given tock.  Note that in the future an event might be a collection of
   * simultaneous events after the given tock, however for now it is simply a single event.
   *
   * @param context flow context
   * @param tock tock to get events for
   * @return event
   */
  public Event<Note> getEvent(FlowContext context, Tock tock) {
    Note note;
    if (tock.getPosition().modulo(Wholes.TWO).equals(Wholes.ZERO)) {
      if (context.getMeasureProvider().getCount(tock.getPosition()).floor() == 0) {
        note = getSequence().getNoteBarStart();
      } else {
        note = getSequence().getNoteBarMid();
      }
      LOG.trace("Creating metronome note {} at {}", note, tock);
      return new DefaultEvent<>(note, tock.getPosition());
    }
    return null;
  }

  @Override
  public Event<Note> getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MovableTock mutableTock = new MovableTock(tock);
    int i = 0;
    long maxScanForward = getMaxScanForward().floor();
    Event<Note> event = null;
    while (event == null && i < maxScanForward) {
      mutableTock.increment();
      event = getEvent(getContext(), mutableTock);
      i++;
    }

    if (event == null) {
      LOG.debug("No notes found in the next {} ticks after tock {} for sequence {}",
          maxScanForward, tock, getSequence());
      event = new ScanForwardEvent<>(mutableTock.getPosition());
    }
    return event;
  }
}
