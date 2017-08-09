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

public class Measure {
  private Map<String, Staff> staves = new HashMap<>();
  private long upper;
  private long lower;

  /**
   * Create a measure in given a time signature
   *
   * @param upper upper part of time signature.
   * @param lower lower part of the time signature.
   */
  public Measure(long upper, long lower) {
    this.upper = upper;
    this.lower = lower;
  }

  public Stream<Event<Note>> eventStream() {
    return staves.values().stream().map(Staff::eventStream)
        .reduce(Stream::concat).orElseGet(Stream::empty);
  }

  public Stream<Map.Entry<String, Staff>> stream() {
    return staves.entrySet().stream();
  }

  public Staff getStaff(String name) {
    return staves.get(name);
  }

  public Staff addStaff(String name) {
    staves.put(name, new Staff(name));
    return staves.get(name);
  }

  public long getUpper() {
    return upper;
  }

  public long getLower() {
    return lower;
  }
}
