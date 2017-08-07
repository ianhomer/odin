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
import com.purplepip.odin.math.CoercedRational;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.composition.CompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.SameTimeUnitTickConverter;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotationFlow extends AbstractFlow<Notation, Note> {
  private Composition composition;
  private TickConverter tickConverter;

  @Override
  public Event<Note> getNextEvent(Tock tock) {
    LOG.debug("Getting next event after {}", tock);
    double compositionTock  = tickConverter.convertBack(tock.getCount().approximateAsDouble());

    Event<Note> nextCompositionEvent =
        composition.getNextEvent(new CoercedRational(compositionTock));
    double flowTock = tickConverter
        .convert(composition.getLoopStart(new CoercedRational(compositionTock))
            .approximateAsDouble()
            + nextCompositionEvent.getTime().approximateAsDouble());
    LOG.debug("Next composition event {} at flow tock {}", nextCompositionEvent, flowTock);
    return new DefaultEvent<>(nextCompositionEvent.getValue(), new CoercedRational(flowTock));
  }

  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising notation flow with {}", getSequence().getNotation());
    composition = new CompositionFactory().create(getSequence().getNotation());
    tickConverter =
        new SameTimeUnitTickConverter(composition::getTick, getClock()::getTick);
  }
}
