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

import java.util.Iterator;
import java.util.stream.Stream;

public abstract class AbstractUnmodifiableThings<T extends Thing> implements Things<T> {
  private Things<T> underlyingThings;

  public AbstractUnmodifiableThings(Things<T> things) {
    this.underlyingThings = things;
  }

  public abstract T unmodifiable(T t);

  @Override
  public Iterator<T> iterator() {
    return stream().iterator();
  }

  @Override
  public int size() {
    return underlyingThings.size();
  }

  @Override
  public Stream<T> stream() {
    return underlyingThings.stream().map(this::unmodifiable);
  }

  @Override
  public ThingStatistics getStatistics() {
    return underlyingThings.getStatistics();
  }

  @Override
  public T findById(long id) {
    return nullOrUnmodifiable(underlyingThings.findById(id));
  }

  @Override
  public T findByName(String name) {
    return nullOrUnmodifiable(underlyingThings.findByName(name));
  }

  private T nullOrUnmodifiable(T thing) {
    return thing == null ? null : unmodifiable(thing);
  }
}
