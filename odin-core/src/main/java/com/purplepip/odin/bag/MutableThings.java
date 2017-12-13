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

package com.purplepip.odin.bag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"statistics", "mutableStatistics"})
public class MutableThings<T extends Thing> implements Things<T> {
  private MutableThingStatistics mutableStatistics = new DefaultThingStatistics();
  private UnmodifiableThingStatistics statistics =
      new UnmodifiableThingStatistics(mutableStatistics);
  private Map<Long, T> things = new HashMap<>();
  private Map<String, T> thingsByName = new HashMap<>();

  /**
   * Add a thing to this mutable bag of things.
   *
   * @param thing thing to add
   * @return true if added AOK
   */
  public boolean add(T thing) {
    mutableStatistics.incrementAddedCount();
    things.put(thing.getId(), thing);
    thingsByName.put(thing.getName(), thing);
    return true;
  }

  @Override
  public int size() {
    return things.size();
  }

  @Override
  public T findById(long id) {
    return things.get(id);
  }

  @Override
  public T findByName(String name) {
    T thing = thingsByName.get(name);
    if (thing == null) {
      LOG.warn("Cannot find thing with name {}", name);
    }
    return thing;
  }

  @Override
  public ThingStatistics getStatistics() {
    return statistics;
  }

  protected void incrementUpdatedCount() {
    mutableStatistics.incrementUpdatedCount();
  }

  /**
   * Remove things matched by predicate.
   *
   * @param filter filter
   * @return true if anything removed
   */
  protected boolean removeIf(Predicate<Map.Entry<Long, T>> filter) {
    /*
     * Remove any thing for which the sequence in the project has been removed.
     */
    int sizeBefore = size();
    boolean result = things.entrySet().removeIf(filter);
    // TODO : Remove from thingsByName too
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
    return things.values().stream();
  }

  /**
   * Iterator for the things so that we can use things in for each loops.
   *
   * @return iterator
   */
  @Override
  public Iterator<T> iterator() {
    return things.values().iterator();
  }

  protected Set<Long> getIds() {
    return new TreeSet<>(things.keySet());
  }

  public void clear() {
    things.clear();
    thingsByName.clear();
  }
}
