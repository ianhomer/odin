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

package com.purplepip.odin.creation.triggers;

import com.purplepip.odin.clock.tick.AbstractTimeThing;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude = "project")
@EqualsAndHashCode(callSuper = true)
@Data
public class GenericTrigger extends AbstractTimeThing implements MutableTriggerConfiguration {
  private String type;
  private Set<String> dependsOn = new HashSet<>();
  private Map<String, String> properties = new HashMap<>();

  public GenericTrigger() {
    super();
  }

  public GenericTrigger(long id) {
    super(id);
  }

  public Stream<String> dependsOn() {
    return dependsOn.stream();
  }

  protected void registerDependency(String dependency) {
    dependsOn.add(dependency);
  }

  protected GenericTrigger copy(GenericTrigger copy, GenericTrigger original) {
    copy.type = original.type;
    super.copy(copy, original);
    return copy;
  }

  @Override
  public GenericTrigger name(String name) {
    super.name(name);
    return this;
  }
}
