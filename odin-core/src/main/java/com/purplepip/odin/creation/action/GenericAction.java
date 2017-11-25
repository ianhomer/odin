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

import com.purplepip.odin.properties.thing.AbstractPropertiesThing;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@Data
public class GenericAction extends AbstractPropertiesThing implements MutableActionConfiguration {
  private String type;
  private Map<String, String> properties = new HashMap<>();

  public GenericAction() {
    super();
  }

  public GenericAction(long id) {
    super(id);
  }

  protected <T extends GenericAction> T copy(T copy, Class<T> type) {
    copy.setType(this.getType());
    super.copy(copy, this);
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
}
