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

package com.purplepip.odin.devices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractHandleProvider implements HandleProvider {
  private final Comparator<Handle> sinkComparator;
  private final Comparator<Handle> sourceComparator;

  public AbstractHandleProvider(
      List<Handle> prioritisedSinks,
      List<Handle> prioritisedSources
  ) {
    sinkComparator = new HandleComparator(prioritisedSinks);
    sourceComparator = new HandleComparator(prioritisedSources);
  }


  protected SortedSet<Handle> asSinkSet(Stream<Handle> stream) {
    return asSet(sinkComparator, stream);
  }

  protected SortedSet<Handle> asSourceSet(Stream<Handle> stream) {
    return asSet(sourceComparator, stream);
  }

  /*
   * Convert stream of handles to set with sorting.
   */
  private SortedSet<Handle> asSet(Comparator<Handle> comparator, Stream<Handle> stream) {
    SortedSet<Handle> handles = new TreeSet<>(comparator);
    handles.addAll(stream.collect(Collectors.toSet()));
    return Collections.unmodifiableSortedSet(handles);
  }


  @Override
  public SortedSet<Handle> getSinkHandles() {
    return asSinkSet(getHandleStream()
        .filter(Handle::isEnabled)
        .filter(Handle::isSink));
  }

  @Override
  public SortedSet<Handle> getSourceHandles() {
    return asSourceSet(getHandleStream()
        .filter(Handle::isEnabled)
        .filter(Handle::isSource));
  }

  protected abstract Stream<Handle> getHandleStream();
}
