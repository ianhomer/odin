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
  private List<Measure> measures = new ArrayList<>();
  private Rational numberOfBeats;

  /**
   * Create composition.
   *
   * @param measures measures to create the composition with
   */
  public Composition(List<Measure> measures) {
    this.measures.addAll(measures);
    this.numberOfBeats = Real.valueOf(measures.stream().mapToLong(measure ->
        measure.getTime().getNumerator()
    ).sum());
  }

  public Tick getTick() {
    return Ticks.BEAT;
  }

  public Stream<Event<Note>> eventStream() {
    return measures.stream().map(Measure::eventStream)
        .reduce(Stream::concat).orElseGet(Stream::empty);
  }

  public Stream<Measure> stream() {
    return measures.stream();
  }

  public int numberOfMeasures() {
    return measures.size();
  }

  public Rational getNumberOfBeats() {
    return numberOfBeats;
  }
}
