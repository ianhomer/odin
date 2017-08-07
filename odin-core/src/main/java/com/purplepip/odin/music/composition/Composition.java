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

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Composition of music.  Tick unit is a beat.
 */
public class Composition {
  private List<Event<Note>> events = new ArrayList<>();
  private Rational tocks;

  public Composition(List<Event<Note>> events, Rational tocks) {
    this.events.addAll(events);
    this.tocks = tocks;
  }

  public Tick getTick() {
    return Ticks.BEAT;
  }

  public Rational getTocks() {
    return tocks;
  }

  public Stream<Event<Note>> stream() {
    return events.stream();
  }

  public int size() {
    return events.size();
  }

  /**
   * Get the tock value for the start of the loop which the given tock is in.
   *
   * @param tock tock
   * @return loop start
   */
  /*
   * TODO : Implement as rational arithmetic.
   */
  public Real getLoopStart(Real tock) {
    return tock.floor(tocks);
  }

  /**
   * Get next event in the composition after the given tock.  Note that if the event is in the
   * next loop then the time for this event will be relative to loop start for the loop in which
   * the given tock is in.
   *
   * @param tock tock from which to find next event*
   * @return next event
   */
  public Event<Note> getNextEvent(Real tock) {
    Real relativeTock = tock.modulo(tocks);
    return
        events.stream().sequential()
            .filter(event -> event.getTime().gt(relativeTock))
            .findFirst()
            .orElseGet(() -> rollOver(events.get(0)));
  }

  /**
   * Roll over the given event to the following loop.
   *
   * @param event event to roll over
   * @return event that has been rolled over
   */
  private Event<Note> rollOver(Event<Note> event) {
    return new DefaultEvent<>(event.getValue(), event.getTime().plus(tocks));
  }
}
