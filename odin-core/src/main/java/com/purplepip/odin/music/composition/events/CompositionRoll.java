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

package com.purplepip.odin.music.composition.events;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.SequentialEventComparator;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.properties.Property;
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Roll based on a composition.
 */
@Slf4j
public class CompositionRoll implements Roll<Note> {
  private Composition composition;
  private List<Event<Note>> events = new ArrayList<>();
  private Event<Note> currentEvent;
  private Real currentLoopStart = Wholes.ZERO;
  private int position;

  /**
   * Create a composition roll from a composition.
   *
   * @param composition composition
   */
  public CompositionRoll(EventsComposition composition) {
    this.composition = composition;
    composition.eventStream().forEach(e -> events.add(e));
    events.sort(new SequentialEventComparator());
    currentEvent = calculateCurrentEvent();
  }

  @Override
  public Event<Note> peek() {
    return getCurrentEvent();
  }

  @Override
  public Event<Note> pop() {
    Event<Note> thisEvent = getCurrentEvent();
    increment();
    return thisEvent;
  }

  private Event<Note> getCurrentEvent() {
    return currentEvent;
  }

  private Event<Note> calculateCurrentEvent() {
    Event<Note> event = events.get(position);
    currentEvent = new DefaultEvent<>(event.getValue(), event.getTime().plus(currentLoopStart));
    return currentEvent;
  }

  private void increment() {
    position++;
    if (position == events.size()) {
      /*
       * Composition starts again
       */
      position = 0;
      currentLoopStart = currentLoopStart.plus(composition.getNumberOfBeats());
      LOG.debug("Composition looped : {}", currentLoopStart);
    }
    /*
     * Calculate the new current event.
     */
    currentEvent = calculateCurrentEvent();
  }


  @Override
  public Property<Tick> getTick() {
    return composition::getTick;
  }
}
