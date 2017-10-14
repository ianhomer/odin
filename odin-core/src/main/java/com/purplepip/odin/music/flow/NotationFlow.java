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
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.composition.events.CompositionRoll;
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.extern.slf4j.Slf4j;

/**
 * Notation flow.
 */
@Slf4j
@FlowDefinition(name = "notation", sequence = Notation.class)
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
    /*
     * If next composition event is in the past then scan the roll forward.
     */
    if (nextCompositionEvent.getTime().le(compositionTock)) {
      compositionRoll.setTock(compositionTock.floorToWhole().plus(Wholes.ONE));
      nextCompositionEvent = compositionRoll.pop();
    }

    while (nextCompositionEvent.getTime().lt(compositionTock)) {
      i++;
      if (i > MAX_EVENT_SCAN) {
        LOG.warn("Max composition scan reached");
        break;
      }
      LOG.warn("Composition event too late : {} < {}",
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
    EventsComposition composition = new NaturalScoreCompositionFactory()
        .create(getSequence().getNotation());
    compositionRoll = new CompositionRoll(composition);
    tickConverter =
        new SameTimeUnitTickConverter(composition::getTick, getClock()::getTick);
  }

  @Override
  public boolean isEmpty() {
    return compositionRoll.isEmpty();
  }

}
