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

package com.purplepip.odin.specificity;

import com.purplepip.odin.properties.Properties;
import com.purplepip.odin.properties.thing.AbstractPropertiesThing;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class GenericThingConfiguration extends AbstractPropertiesThing
    implements MutableThingConfiguration {
  private String type;

  public GenericThingConfiguration() {
    super();
  }

  public GenericThingConfiguration(String name) {
    super(name);
  }

  public GenericThingConfiguration(long id) {
    super(id);
  }

  @Override
  public GenericThingConfiguration copy() {
    GenericThingConfiguration copy = new GenericThingConfiguration(this.getId());
    Properties.copyProperties(this, copy);
    return copy;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  @NotNull
  public String getType() {
    return type;
  }
}
