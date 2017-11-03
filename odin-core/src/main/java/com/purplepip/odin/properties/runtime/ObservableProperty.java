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

package com.purplepip.odin.properties.runtime;

import java.util.HashSet;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = "observers")
public class ObservableProperty<T> implements Mutable<T>, Observable {
  private T value;
  private Set<Observer> observers = new HashSet<>();

  /**
   * Create a property without setting an initial value.
   */
  public ObservableProperty() {
    /*
     * Empty constructor to explicitly differentiate with constructor that sets value.
     */
  }

  public ObservableProperty(T value) {
    set(value);
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public final void set(T value) {
    LOG.debug("Changing property to {}", value);
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
