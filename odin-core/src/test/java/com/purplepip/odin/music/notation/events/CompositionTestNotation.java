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

package com.purplepip.odin.music.notation.events;

import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.notes.Note;

/**
 * Notation for internal composition structure used only for test assertion purposes.  This
 * notation is simple to serialise for comparison, but not designed for human readability.
 */
public class CompositionTestNotation {
  private final String notation;

  public CompositionTestNotation(EventsComposition composition) {
    notation = createNotation(composition);
  }

  private String createNotation(EventsComposition composition) {
    StringBuilder builder = new StringBuilder(128);
    composition.eventStream().forEachOrdered(event -> {
          Note note = (Note) event.getValue();
          builder
              .append(event.getTime())
              .append(".")
              .append(note.getDuration())
              .append("-")
              .append(note.getNumber()).append(" ");
        }
    );
    return builder.toString();
  }

  public String getBody() {
    return notation;
  }
}
