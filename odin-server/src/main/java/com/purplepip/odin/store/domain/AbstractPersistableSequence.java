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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.sequence.MutableSequence;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
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
@EqualsAndHashCode(exclude = {"id", "version", "project"})
@ToString(exclude = "project")
@Slf4j
public abstract class AbstractPersistableSequence implements MutableSequence {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  public long id;

  @Version
  @JsonIgnore
  private Long version;

  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @JsonIgnore
  @Transient
  private Set<Layer> layers;

  @Override
  public void removeLayer(Layer layer) {
    layers.remove(layer);
  }

  @Override
  public void addLayer(Layer layer) {
    layers.add(layer);
  }

  @PrePersist
  public void addToProject() {
    project.addSequence(this);
  }

  @PreRemove
  public void removeFromProject() {
    project.removeSequence(this);
  }
}
