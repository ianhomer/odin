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

package com.purplepip.odin.properties.thing;

import com.purplepip.odin.bag.AbstractThing;
import com.purplepip.odin.bag.Copyable;
import com.purplepip.odin.common.Stringy;
import com.purplepip.odin.properties.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;

/**
 * Abstract thing that can store properties.
 */
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPropertiesThing extends AbstractThing
    implements MutablePropertiesThing, Copyable {
  private Map<String, String> properties = new HashMap<>();

  public AbstractPropertiesThing() {
    super();
  }

  public AbstractPropertiesThing(long id) {
    super(id);
  }

  @Override
  public String getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return properties.keySet().stream();
  }

  @Override
  public Stream<Map.Entry<String, String>> getPropertyEntries() {
    return properties.entrySet().stream();
  }

  public boolean hasProperties() {
    return !properties.isEmpty();
  }

  @Override
  public void setProperty(String propertyName, String value) {
    properties.put(propertyName, value);
  }

  public AbstractPropertiesThing property(String propertyName, String value) {
    setProperty(propertyName, value);
    return this;
  }

  protected MutablePropertiesThing copy(MutablePropertiesThing copy) {
    Properties.copyProperties(this, copy);
    super.copy(copy);
    return copy;
  }

  /**
   * To string.
   *
   * @return to string
   */
  @Override
  public String toString() {
    return Stringy.of(AbstractPropertiesThing.class, this)
        .add("name", getName())
        .add("properties", properties.entrySet().stream())
        .build();
  }
}
