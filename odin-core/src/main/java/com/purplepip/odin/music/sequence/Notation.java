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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.IndexedComposition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.SpecialisedSequence;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.Loop;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Name("notation")
/*
 * TODO : Simplify constructor and copy implementation as per Pattern and Random.
 */
public class Notation extends GenericSequence implements SpecialisedSequence {
  private String format;
  private String notation;
  private IndexedComposition indexedComposition;

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    LOG.debug("Getting notation event at {}", loop);
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

  /**
   * Initialisation after properties are set.
   */
  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising notation flow with {}", getNotation());
    EventsComposition composition = new NaturalScoreCompositionFactory()
        .create(getNotation());
    indexedComposition = new IndexedComposition(composition);
    super.afterPropertiesSet();
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  public Notation copy() {
    return copy(new Notation(), this);
  }

  protected Notation copy(Notation copy, Notation original) {
    copy.notation = this.notation;
    copy.format = this.format;
    super.copy(copy, original);
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return notation == null || notation.length() == 0 || super.isEmpty()
        || indexedComposition.isEmpty();
  }

  @Override
  public Rational getLoopLength() {
    if (super.getLength() < 0 && indexedComposition != null) {
      /*
       * Length is taken from composition.
       */
      return indexedComposition.getLength();
    }
    return Whole.valueOf(super.getLength());
  }

  public Notation notation(String notation) {
    this.notation = notation;
    return this;
  }
}
