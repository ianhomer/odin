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
import com.purplepip.odin.music.notes.Note;
import java.util.ArrayList;
import java.util.List;

public class CompositionBuilder {
  private List<Event<Note>> events = new ArrayList<>();
  private int length;

  public void addEvent(Event<Note> event) {
    events.add(event);
  }

  public void setLength(int length) {
    this.length = length;
  }

  public Composition create() {
    return new Composition(events, length);
  }
}
