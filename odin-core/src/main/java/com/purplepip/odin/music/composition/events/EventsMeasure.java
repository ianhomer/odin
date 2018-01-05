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
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.music.composition.Measure;
import com.purplepip.odin.music.notes.Note;
import java.util.stream.Stream;

/**
 * Measure based on events.
 */
public class EventsMeasure extends Measure<EventsStaff> {
  public EventsMeasure(Rational time, String key) {
    super(time, key);
  }

  Stream<Event> eventStream() {
    return stream().map(EventsStaff::eventStream)
        .reduce(Stream::concat).orElseGet(Stream::empty);
  }
}
