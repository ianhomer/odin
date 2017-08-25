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

package com.purplepip.odin.events;

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import java.util.List;

/**
 * Cheap string serialisation for test assertions.
 */
public class EventsListStringifier {
  private List<Event> events;

  public EventsListStringifier(List<Event> events) {
    this.events = events;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(128);
    events.forEach(e ->
        builder.append(e.getTime()).append("=").append(toString(e.getValue())).append(";")
    );
    return builder.toString();
  }

  private String toString(Object o) {
    if (o instanceof Note) {
      Note note = (Note) o;
      StringBuilder builder = new StringBuilder(128);
      builder.append(note.getNumber()).append('-');
      if (note.getVelocity() != Notes.DEFAULT_VELOCITY) {
        builder.append(note.getVelocity());
      }
      builder.append('-');
      if (!note.getDuration().equals(Notes.DEFAULT_DURATION)) {
        builder.append(note.getDuration());
      }
      return builder.toString();
    }
    return o.toString();
  }
}
