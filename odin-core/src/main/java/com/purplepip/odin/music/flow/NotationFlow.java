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
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.composition.CompositionRoll;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.extern.slf4j.Slf4j;

/*
 * TODO : Should we force flow tock to equal composition tock to equal a beat?  This will
 * make this implementation simpler and I don't think restrictive in anyway.
 */
@Slf4j
public class NotationFlow extends AbstractFlow<Notation, Note> {
  private static final int MAX_EVENT_SCAN = 1000;
  private CompositionRoll compositionRoll;
  private TickConverter tickConverter;

  @Override
  public Event<Note> getNextEvent(Tock tock) {
    LOG.debug("Getting next event after {}", tock);
    Real compositionTock  = tickConverter.convertBack(tock.getPosition());
    Event<Note> nextCompositionEvent = compositionRoll.pop();
    int i = 0;
    while (nextCompositionEvent.getTime().le(compositionTock)) {
      i++;
      if (i > MAX_EVENT_SCAN) {
        LOG.warn("Max composition scan reached");
        break;
      }
      LOG.warn("Composition event too late : {} <= {}",
          nextCompositionEvent.getTime(), compositionTock);
      nextCompositionEvent = compositionRoll.pop();
    }
    Real flowTock = tickConverter.convert(nextCompositionEvent.getTime());
    LOG.debug("Next composition event {} at flow tock {}", nextCompositionEvent, flowTock);
    return new DefaultEvent<>(nextCompositionEvent.getValue(), flowTock);
  }

  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising notation flow with {}", getSequence().getNotation());
    Composition composition = new NaturalScoreCompositionFactory()
        .create(getSequence().getNotation());
    compositionRoll = new CompositionRoll(composition);
    tickConverter =
        new SameTimeUnitTickConverter(composition::getTick, getClock()::getTick);
  }
}
