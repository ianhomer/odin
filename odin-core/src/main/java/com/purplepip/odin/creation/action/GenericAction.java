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

package com.purplepip.odin.creation.action;

import com.purplepip.odin.common.Stringy;
import com.purplepip.odin.properties.thing.AbstractPropertiesThing;
import com.purplepip.odin.specificity.NameValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenericAction extends AbstractPropertiesThing implements MutableActionConfiguration {
  private final String type;

  /**
   * Create a new generic action.
   */
  GenericAction() {
    super();
    type = new NameValue(getClass()).get();
  }

  public GenericAction(String type) {
    super();
    this.type = type;
  }

  public GenericAction(String type, long id) {
    super(id);
    this.type = type;
  }

  protected <T extends GenericAction> T copy(T copy, Class<T> type) {
    super.copy(copy);
    return copy;
  }

  public String getType() {
    return type;
  }

  public GenericAction name(String name) {
    super.setName(name);
    return this;
  }

  public GenericAction property(String propertyName, String value) {
    super.property(propertyName, value);
    return this;
  }

  /**
   * Convert this to a string.
   *
   * @return object as string
   */
  public String toString() {
    return Stringy.of(GenericAction.class, this)
        .add("type", type)
        .add("name", getName())
        .add("properties", getPropertyEntries())
        .build();
  }
}
