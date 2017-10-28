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

package com.purplepip.odin.sequence.triggers;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.tick.AbstractTimeThing;
import com.purplepip.odin.sequencer.Operation;
import java.util.HashMap;
import java.util.Map;
import lombok.ToString;

@ToString(exclude = "project")
public class GenericTrigger extends AbstractTimeThing implements MutableTriggerConfiguration {
  private Project project;
  private String triggerRule;

  public GenericTrigger() {
    super();
  }

  public GenericTrigger(String name) {
    super(name);
  }

  public GenericTrigger(long id) {
    super(id);
  }

  private Map<String, String> properties = new HashMap<>();

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setTriggerRule(String triggerRule) {
    this.triggerRule = triggerRule;
  }

  @Override
  public String getTriggerRule() {
    return triggerRule;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    return false;
  }
}
