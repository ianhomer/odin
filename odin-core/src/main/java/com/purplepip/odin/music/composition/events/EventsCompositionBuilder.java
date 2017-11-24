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

package com.purplepip.odin.music.composition.events;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventsCompositionBuilder {
  private static final Rational DEFAULT_TIME = new Rational(4, 4, false);
  private static final String DEFAULT_KEY = "C";
  private static final String DEFAULT_CLEF_NAME = "treble";

  private List<EventsMeasure> measures = new ArrayList<>();
  private EventsMeasure currentMeasure;
  private EventsStaff currentStaff;
  private EventsVoice currentVoice;
  private Rational positionInComposition = Wholes.ZERO;
  private Rational positionInMeasure;
  private int minimumMeasures;

  private void beforeAddNote() {
    if (currentMeasure == null) {
      startMeasure();
    } else {
      /*
       * Start new measure if we've come to the end of the previous one
       */
      if (positionInMeasure.equals(currentMeasure.getNumberOfBeats())) {
        startMeasure();
      }
    }

    if (currentStaff == null) {
      currentStaff = new EventsStaff(DEFAULT_CLEF_NAME);
      currentMeasure.addStaff(currentStaff);
    }

    if (currentVoice == null) {
      currentVoice = new EventsVoice();
      currentStaff.addVoice(currentVoice);
    }
  }

  /**
   * Start measure.
   *
   * @return this
   */
  public EventsCompositionBuilder startMeasure() {
    if (currentMeasure == null) {
      return startMeasure(DEFAULT_TIME, DEFAULT_KEY);
    }
    return startMeasure(currentMeasure.getTime(), currentMeasure.getKey());
  }

  /**
   * Add a measure to the composition.
   *
   * @param time time signature
   * @param key key signature
   * @return this
   */
  public EventsCompositionBuilder startMeasure(Rational time, String key) {
    positionInMeasure = Wholes.ZERO;
    currentMeasure = new EventsMeasure(time, key);
    /*
     * Reset staff and voice since new ones will need to be created for this new measure.
     */
    currentStaff = null;
    currentVoice = null;
    measures.add(currentMeasure);
    return this;
  }

  public EventsMeasure getCurrentMeasure() {
    return currentMeasure;
  }

  /**
   * Add a note to the composition.
   *
   * @param note note
   * @return this
   */
  public EventsCompositionBuilder addNote(Note note) {
    beforeAddNote();
    Real remaining = currentMeasure.getNumberOfBeats().minus(positionInMeasure);
    LOG.debug("Tock {} ; Measure {} ; Time {} ; Remaining {} ; Duration {}",
        positionInComposition, positionInMeasure,
        currentMeasure.getTime(), remaining, note.getDuration());
    if (remaining.lt(note.getDuration())) {
      /*
       * If the note goes over the end of the current measure, then split the note in two so that
       * they are place in consecutive measures.
       */
      addNote(note.copyWithNewDuration(remaining));
      addNote(note.copyWithNewDuration(note.getDuration().minus(remaining)));
    } else {
      Event<Note> event = new DefaultEvent<>(note, positionInComposition);
      currentVoice.addEvent(event);
      positionInMeasure = positionInMeasure.plus(note.getDuration().toRational());
      positionInComposition = positionInComposition.plus(note.getDuration().toRational());
    }
    return this;
  }

  /**
   * Create composition.
   *
   * @return composition
   */
  public EventsComposition create() {
    for (int i = measures.size() ; i < minimumMeasures ; i++) {
      LOG.trace("Auto creating measure to reach minimum of {}", i);
      startMeasure();
    }
    LOG.trace("Creating composition with {} measures", measures.size());
    return new EventsComposition(measures);
  }

  public EventsCompositionBuilder withMinimumMeasures(int minimumMeasures) {
    this.minimumMeasures = minimumMeasures;
    return this;
  }
}
