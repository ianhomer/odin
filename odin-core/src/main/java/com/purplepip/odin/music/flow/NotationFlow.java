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
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.IndexedComposition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.flow.Loop;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Notation flow.
 */
@Slf4j
@FlowDefinition(name = "notation", sequence = Notation.class)
public class NotationFlow extends AbstractFlow<Notation, Note> {
  private IndexedComposition indexedComposition;

  public NotationFlow(Clock clock, MeasureProvider measureProvider) {
    super(clock, measureProvider);
  }

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    LOG.debug("Getting event at {}", loop);
    Event<Note> event = indexedComposition.getEventAfter(loop.getPosition());
    if (event != null) {
      /*
       * Use the absolute tock position for the returned event.
       */
      Real eventTime = event.getTime().plus(loop.getStart());
      LOG.debug("Returning event at tock {}", eventTime);
      return new DefaultEvent<>(event.getValue(), eventTime);
    }
    return null;
  }

  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising notation flow with {}", getSequence().getNotation());
    EventsComposition composition = new NaturalScoreCompositionFactory()
        .create(getSequence().getNotation());
    indexedComposition = new IndexedComposition(composition);
  }

  @Override
  public Real getLength() {
    return indexedComposition.getLength();
  }

  @Override
  public boolean isEmpty() {
    return indexedComposition.isEmpty();
  }

}
