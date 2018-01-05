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

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Bound;
import com.purplepip.odin.math.Rational;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Index of notes for each whole number tock of a composition.
 */
@Slf4j
public class IndexedComposition {
  /*
   * Events indexed on the integer
   */
  private HashMap<Long, List<Event>> events = new HashMap<>();
  private Rational length;

  /**
   * Create an indexed composition.
   *
   * @param eventsComposition composition to create index from
   */
  public IndexedComposition(EventsComposition eventsComposition) {
    eventsComposition.eventStream().forEachOrdered(event -> {
          long index = event.getTime().floor();
          List<Event> eventsAtIndex =
              events.computeIfAbsent(index, k -> new ArrayList<>());
          eventsAtIndex.add(event);
        }
    );
    length = eventsComposition.getNumberOfBeats();
  }

  /**
   * Get event after the given bound.
   *
   * @param bound number boundary
   * @return next event
   */
  public Event getEventAfter(Bound bound) {
    long floor = bound.getLimit().floor();
    List<Event> eventsAtIndex = events.get(floor);
    if (eventsAtIndex != null) {
      for (Event event : eventsAtIndex) {
        LOG.trace("{} events found at tock {}", eventsAtIndex.size(), floor);
        if (bound.lt(event.getTime())) {
          LOG.trace("Returning event at {}", event.getTime());
          return event;
        }
      }
      LOG.trace("No more events found below whole ceiling of tock {}", floor);
    } else {
      LOG.trace("No events found at tock {}", floor);
    }
    return null;
  }

  public boolean isEmpty() {
    return events.isEmpty();
  }

  public Rational getLength() {
    return length;
  }
}
