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

package com.purplepip.odin.music.composition;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.music.notes.Note;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompositionBuilder {
  private static final int DEFAULT_TIME_UPPER = 4;
  private static final int DEFAULT_TIME_LOWER = 4;
  private static final String DEFAULT_STAFF_NAME = "treble";
  private static final String DEFAULT_VOICE_NAME = "default";

  private List<Measure> measures = new ArrayList<>();
  private Rational length;
  private Measure currentMeasure;
  private Staff currentStaff;
  private Voice currentVoice;

  private void beforeAddEvent() {
    if (currentMeasure == null) {
      addMeasure(DEFAULT_TIME_UPPER, DEFAULT_TIME_LOWER);
    }
    if (currentStaff == null) {
      currentStaff = currentMeasure.addStaff(DEFAULT_STAFF_NAME);
    }
    if (currentVoice == null) {
      currentVoice = currentStaff.addVoice(DEFAULT_VOICE_NAME);
    }
  }

  /**
   * Add a measure to the composition.
   *
   * @param upper upper part of the time signature
   * @param lower lower part of the time signature
   * @return this
   */
  public CompositionBuilder addMeasure(long upper, long lower) {
    currentMeasure = new Measure(upper, lower);
    measures.add(currentMeasure);
    return this;
  }

  /**
   * Add a note event to the composition.
   *
   * @param event note event
   * @return this
   */
  public CompositionBuilder addEvent(Event<Note> event) {
    beforeAddEvent();
    currentVoice.addEvent(event);
    return this;
  }

  public Composition create() {
    LOG.debug("Creating composition with length {}", length);
    return new Composition(measures);
  }
}
