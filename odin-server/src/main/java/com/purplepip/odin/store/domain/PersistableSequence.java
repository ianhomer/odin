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

import com.purplepip.odin.creation.sequence.Action;
import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration;
import com.purplepip.odin.project.Project;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable sequence.
 */
@Entity(name = "Sequence")
@Table(name = "Sequence")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@ToString(exclude = "project", callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class PersistableSequence
    extends PersistableTimeThing implements MutableSequenceConfiguration {
  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @NotNull
  private String type;

  private int channel;

  @ElementCollection
  private List<String> layers = new ArrayList<>(0);

  @ElementCollection
  private Map<String, Action> triggers = new HashMap<>(0);

  @Override
  public void addLayer(String layer) {
    LOG.debug("Adding layer {} to {}", layer, this);
    layers.add(layer);
  }

  @Override
  public void removeLayer(String layer) {
    LOG.debug("Removing layer {} from {}", layer, this);
    layers.remove(layer);
  }

  @Override
  public void addTrigger(String trigger, Action action) {
    LOG.debug("Adding trigger {} to {}", trigger, this);
    triggers.put(trigger, action);
  }

  @Override
  public void removeTrigger(String trigger) {
    LOG.debug("Removing trigger {} from {}", trigger, this);
    triggers.remove(trigger);
  }

  @PreUpdate
  public void preUpdate() {
    PersistableHelper.removeDuplicates(layers);
  }

  @PrePersist
  public void prePesist() {
    addToProject();
  }

  public void addToProject() {
    project.addSequence(this);
  }

  @PreRemove
  public void removeFromProject() {
    project.removeSequence(this);
  }

  @Override
  public boolean isEmpty() {
    return getLayers().isEmpty();
  }
}
