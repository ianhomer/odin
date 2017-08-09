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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Staff {
  private String name;
  private Map<String, Voice> voices = new HashMap<>();

  public Staff(String name) {
    this.name = name;
  }

  public Stream<Event<Note>> eventStream() {
    return voices.values().stream().map(Voice::stream)
        .reduce(Stream::concat).orElseGet(Stream::empty);
  }

  public Stream<Map.Entry<String, Voice>> stream() {
    return voices.entrySet().stream();
  }

  public String getName() {
    return name;
  }

  public Voice getVoice(String name) {
    return voices.get(name);
  }

  public Voice addVoice(String name) {
    voices.put(name, new Voice(name));
    return voices.get(name);
  }
}
