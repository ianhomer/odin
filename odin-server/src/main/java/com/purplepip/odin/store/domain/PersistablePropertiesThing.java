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

package com.purplepip.odin.store.domain;

import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = "PropertiesThing")
@EqualsAndHashCode(callSuper = true)
@ToString()
public class PersistablePropertiesThing extends PersistableThing
    implements MutablePropertiesProvider {
  @ElementCollection
  private Map<String, String> properties = new HashMap<>(0);

  @Override
  public void setProperty(String name, String value) {
    properties.put(name, value);
  }

  @Override
  public String getProperty(String name) {
    return properties.get(name);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return properties.keySet().stream();
  }

  @Override
  public Stream<Map.Entry<String, String>> getPropertyEntries() {
    return properties.entrySet().stream();
  }

  @Override
  public boolean hasProperties() {
    return properties.isEmpty();
  }
}
