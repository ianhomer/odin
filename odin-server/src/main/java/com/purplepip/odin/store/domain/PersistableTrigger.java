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

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.sequencer.Operation;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable trigger.
 */
@Data
@Entity(name = "Trigger")
@Table(name = "Trigger")
@ToString(exclude = "project", callSuper = true)
@EqualsAndHashCode(exclude = {"project"}, callSuper = true)
@Slf4j
public class PersistableTrigger extends PersistableTimeThing
    implements MutableTriggerConfiguration {
  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @NotNull
  private String triggerRule;

  @Override
  public boolean isTriggeredBy(Operation operation) {
    // TODO : Is trigger by logic needs to decouple from model so that we can persist
    // triggers generically and trigger appropriate trigger logic.
    return false;
  }
}
