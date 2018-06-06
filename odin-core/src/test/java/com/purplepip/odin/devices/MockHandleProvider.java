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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MockHandleProvider extends AbstractHandleProvider {
  private final boolean hasSinks;
  private final boolean hasSources;
  private final Predicate<Handle> predicate;

  /**
   * Create Mock Handle Provider.
   *
   * @param hasSinks has sinks
   * @param hasSources has sources
   * @param prioritisedSinks list of prioritised sinks
   * @param prioritisedSources list of prioritised sources
   */
  public MockHandleProvider(boolean hasSinks, boolean hasSources,
                            List<Handle> prioritisedSinks, List<Handle> prioritisedSources) {
    this(hasSinks, hasSources, prioritisedSinks, prioritisedSources, (handle) -> true);
  }

  /**
   * Create Mock Handle Provider.
   *
   * @param hasSinks has sinks
   * @param hasSources has sources
   * @param prioritisedSinks list of prioritised sinks
   * @param prioritisedSources list of prioritised sources
   * @param predicate filter for sinks / sources that should be returned
   */
  public MockHandleProvider(boolean hasSinks, boolean hasSources,
                            List<Handle> prioritisedSinks, List<Handle> prioritisedSources,
                            Predicate<Handle> predicate) {
    super(prioritisedSinks, prioritisedSources);
    this.hasSinks = hasSinks;
    this.hasSources = hasSources;
    this.predicate = predicate;
  }

  @Override
  protected Stream<Handle> getHandleStream() {
    Stream.Builder<Handle> builder = Stream.builder();
    if (hasSinks && hasSources) {
      builder.accept(createHandle("TTTXXX", true, true, true, false));
    }
    if (hasSinks) {
      builder.accept(createHandle("TFTCCC", true, false, true));
    }
    if (hasSources) {
      builder.accept(createHandle("FTTBBB", false, true, true));
    }
    if (hasSinks && hasSources) {
      builder
          .add(createHandle("TTTAAA", true, true, true))
          .add(createHandle("TTFDDD", true, true, false))
          .add(createHandle("TTTEEE", true, true, true))
          .add(createHandle("TTTFFF", true, true, true));
    }
    return builder.build().filter(predicate);
  }

  @Override
  public Set<Class<? extends Handle>> getHandleClasses() {
    Set<Class<? extends Handle>> handleClasses = new HashSet<>();
    handleClasses.add(MockHandle.class);
    return handleClasses;
  }

  private static Handle createHandle(String name, boolean sink, boolean source,
                                     boolean enabled, boolean openOk) {
    return new MockHandle(name, sink, source, enabled, openOk);
  }

  private static Handle createHandle(String name, boolean sink, boolean source, boolean enabled) {
    return new MockHandle(name, sink, source, enabled, true);
  }
}
