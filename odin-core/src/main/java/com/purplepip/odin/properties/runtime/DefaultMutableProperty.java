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

import lombok.extern.slf4j.Slf4j;

/**
 * A property that can change.
 *
 * @param <T> type of property
 */
@Slf4j
public class DefaultMutableProperty<T> implements MutableProperty<T> {
  private T value;

  /**
   * Create a property without setting an initial value.
   */
  public DefaultMutableProperty() {
    /*
     * Empty constructor to explicitly differentiate with constructor that sets value.
     */
  }

  public DefaultMutableProperty(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(T value) {
    LOG.debug("Changing property to {}", value);
    this.value = value;
  }
}
