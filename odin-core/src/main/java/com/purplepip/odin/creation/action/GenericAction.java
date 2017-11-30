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
  private String typeFromAnnotation = new NameValue(this).get();
  private String type = typeFromAnnotation;

  /**
   * Create a new generic action.
   */
  public GenericAction() {
    super();
  }

  public GenericAction(long id) {
    super(id);
  }

  protected <T extends GenericAction> T copy(T copy, Class<T> type) {
    copy.setType(this.getType());
    super.copy(copy);
    return copy;
  }

  public GenericAction type(String type) {
    this.type = type;
    return this;
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
  // TODO : Wrap this toString logic up in utility class ready for reuse.
  public String toString() {
    Stringy stringy = Stringy.of(GenericAction.class);
    if (type == null || !type.equals(typeFromAnnotation)) {
      stringy.add("type", getType());
    }
    if (getName() != null) {
      stringy.add("name", this.getName());
    }
    if (hasProperties()) {
      stringy.add("properties", getPropertyEntries());
    }
    return stringy.build();
  }
}
