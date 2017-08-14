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

package com.purplepip.odin.bag;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableThings<T extends Thing> implements Things<T> {
  private MutableThingStatistics mutableStatistics = new DefaultThingStatistics();
  private UnmodifiableThingStatistics statistics =
      new UnmodifiableThingStatistics(mutableStatistics);
  private Set<T> things = new HashSet<>();

  public boolean add(T thing) {
    mutableStatistics.incrementAddedCount();
    return things.add(thing);
  }

  @Override
  public int size() {
    return things.size();
  }

  @Override
  public ThingStatistics getStatistics() {
    return statistics;
  }

  public void incrementUpdatedCount() {
    mutableStatistics.incrementUpdatedCount();
  }

  /**
   * Remove things matched by predicate.
   *
   * @param filter filter
   * @return true if anything removed
   */
  public boolean removeIf(Predicate<Thing> filter) {
    /*
     * Remove any track for which the sequence in the project has been removed.
     */
    int sizeBefore = size();
    boolean result = things.removeIf(filter);
    if (result) {
      int removalCount = sizeBefore - size();
      LOG.debug("Removed {} tracks, ", removalCount);
      mutableStatistics.incrementRemovedCount(removalCount);
    } else {
      LOG.debug("No sequence track detected for removal out of {}", size());
    }

    return result;
  }

  @Override
  public Stream<T> stream() {
    return things.stream();
  }

  @Override
  public Iterator<T> iterator() {
    return things.iterator();
  }
}
