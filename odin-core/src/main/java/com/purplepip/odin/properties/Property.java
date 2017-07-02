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

package com.purplepip.odin.properties;

import java.util.HashSet;
import java.util.Set;

public class Property<T> implements Mutable<T> {
  private T value;
  private Set<Observer> observers = new HashSet<>();

  public Property(T value) {
    set(value);
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(T value) {
    this.value = value;
    observers.forEach(Observer::onChange);
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }
}
