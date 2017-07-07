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

package com.purplepip.odin.sequencer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MutableTracks implements Tracks {
  private Set<Track> tracks = new HashSet<>();

  boolean add(Track track) {
    return tracks.add(track);
  }

  public int size() {
    return tracks.size();
  }

  boolean removeIf(Predicate<Track> filter) {
    return tracks.removeIf(filter);
  }

  Stream<Track> stream() {
    return tracks.stream();
  }

  @Override
  public Iterator<Track> iterator() {
    return tracks.iterator();
  }
}
