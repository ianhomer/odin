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
import com.purplepip.odin.music.notes.Note;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Measure {
  private List<Staff> staves = new ArrayList<>();
  private Rational time;
  private String key;

  /**
   * Create a measure in given a time signature and for a given key
   *
   * @param time time signature
   * @param key key signature.
   */
  public Measure(Rational time, String key) {
    this.time = time;
    this.key = key;
  }

  Stream<Event<Note>> eventStream() {
    return staves.stream().map(Staff::eventStream)
        .reduce(Stream::concat).orElseGet(Stream::empty);
  }

  public Stream<Staff> stream() {
    return staves.stream();
  }

  Staff addStaff(String clef) {
    Staff staff = new Staff(clef);
    staves.add(staff);
    return staff;
  }

  public String getKey() {
    return key;
  }

  public Rational getTime() {
    return time;
  }
}
