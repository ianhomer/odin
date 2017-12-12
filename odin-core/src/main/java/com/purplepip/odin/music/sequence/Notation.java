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

import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.creation.sequence.SequencePlugin;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.IndexedComposition;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(callSuper = true, exclude = "indexedComposition")
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Name("notation")
public class Notation extends SequencePlugin {
  private String format;
  private String notation;
  private transient IndexedComposition indexedComposition;
  private transient String indexedCompositionNotation;

  @Override
  public Event<Note> getNextEvent(MeasureContext context, Loop loop) {
    Event<Note> event = indexedComposition.getEventAfter(loop.getPosition());
    if (event != null) {
      /*
       * Use the absolute tock position for the returned event.
       */
      Real eventTime = loop.getAbsolutePosition(event.getTime());
      return new DefaultEvent<>(event.getValue(), eventTime);
    }
    return null;
  }

  /**
   * Initialisation which must be run before this sequence can be used.
   */
  @Override
  public void initialise() {
    LOG.debug("Initialising notation for {}", this);
    /*
     * Only reinitialise indexed composition if notation has changed
     */
    if (notation != null && !notation.equals(indexedCompositionNotation)) {
      initialiseComposition();
    }
    super.initialise();
  }

  private void initialiseComposition() {
    indexedCompositionNotation = notation;
    EventsComposition composition = new NaturalScoreCompositionFactory().create(notation);
    indexedComposition = new IndexedComposition(composition);
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Notation copy() {
    return copy(new Notation());
  }

  protected Notation copy(Notation copy) {
    copy.notation = this.notation;
    copy.format = this.format;
    super.copy(copy);
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return notation == null || notation.length() == 0 || super.isEmpty()
        || indexedComposition == null
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

  public Notation format(String format) {
    this.format = format;
    return this;
  }
}
