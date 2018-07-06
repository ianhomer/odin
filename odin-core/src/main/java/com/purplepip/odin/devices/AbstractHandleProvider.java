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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractHandleProvider implements HandleProvider {
  private final Comparator<Handle> sinkComparator;
  private final Comparator<Handle> sourceComparator;
  private final Set<String> excludeSinks;
  private final Set<String> excludeSources;

  AbstractHandleProvider(
      List<Handle> prioritisedSinks,
      List<Handle> prioritisedSources) {
    this(prioritisedSinks, prioritisedSources, Collections.emptySet(), Collections.emptySet());
  }

  /**
   * Create abstract handle provider.
   *
   * @param prioritisedSinks prioritised list of sinks
   * @param prioritisedSources prioritised list of sources
   * @param excludeSinks sinks to be excluded
   * @param excludeSources sources to be excluded
   */
  public AbstractHandleProvider(
      List<Handle> prioritisedSinks,
      List<Handle> prioritisedSources,
      Set<Handle> excludeSinks,
      Set<Handle> excludeSources
  ) {
    sinkComparator = new HandleComparator(prioritisedSinks);
    sourceComparator = new HandleComparator(prioritisedSources);
    this.excludeSinks = excludeSinks.stream().map(Handle::getName).collect(Collectors.toSet());
    this.excludeSources = excludeSources.stream().map(Handle::getName).collect(Collectors.toSet());
    LOG.debug("Created handle provider {} with excluded sinks {} and excluded sources {}",
        this, excludeSinks, excludeSources);
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
        .filter(handle -> !excludeSinks.contains(handle.getName()))
        .filter(Handle::isEnabled)
        .filter(Handle::isSink));
  }

  @Override
  public SortedSet<Handle> getSourceHandles() {
    return asSourceSet(getHandleStream()
        .filter(handle -> !excludeSources.contains(handle.getName()))
        .filter(Handle::isEnabled)
        .filter(Handle::isSource));
  }

  protected abstract Stream<Handle> getHandleStream();
}
