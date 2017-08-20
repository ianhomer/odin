/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.TimeUnit;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    extends PersistableThing implements MutableSequence {
  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @NotNull
  private String flowName;

  private int channel;

  @Column(name = "o")
  private long offset;

  private long length;

  @OneToOne(targetEntity = PersistableTick.class, cascade = CascadeType.ALL, orphanRemoval = true)
  @NotNull
  private Tick tick;

  @ElementCollection
  private Map<String, String> properties = new HashMap<>(0);

  @ElementCollection
  private List<String> layers = new ArrayList<>(0);

  @Override
  public void removeLayer(String layer) {
    LOG.debug("Removing layer {} from {}", layer, this);
    layers.remove(layer);
  }

  @Override
  public void setProperty(String name, String value) {
    properties.put(name, value);
  }

  @Override
  public void addLayer(String layer) {
    LOG.debug("Adding layer {} to {}", layer, this);
    layers.add(layer);
  }

  @PreUpdate
  public void preUpdate() {
    PersistableHelper.removeDuplicates(layers);
  }


  public void addToProject() {
    project.addSequence(this);
  }

  /**
   * Set default values.
   */
  private void setDefaults() {
    if (tick == null) {
      PersistableTick newTick = new PersistableTick();
      newTick.setTimeUnit(TimeUnit.BEAT);
      newTick.setNumerator(1);
      newTick.setDenominator(1);
      tick = newTick;
    }
  }

  @PrePersist
  public void prePesist() {
    setDefaults();
    addToProject();
  }

  @PreRemove
  public void removeFromProject() {
    project.removeSequence(this);
  }

  @Override
  public String getProperty(String name) {
    return properties.get(name);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return properties.keySet().stream();
  }
}
