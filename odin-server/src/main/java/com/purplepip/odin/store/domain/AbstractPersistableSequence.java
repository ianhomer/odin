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
import com.purplepip.odin.sequence.MutableSequence;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Version;
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
@EqualsAndHashCode(of = "id")
@ToString(exclude = "project")
@Slf4j
public abstract class AbstractPersistableSequence implements MutableSequence {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  public long id;

  private String name;

  @Version
  @JsonIgnore
  private Long version;

  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @Column(name = "o")
  private long offset;

  private long length;

  @OneToOne(targetEntity = PersistableTick.class, cascade = CascadeType.ALL, orphanRemoval = true)
  @NotNull
  private Tick tick;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> layers = new ArrayList<>(0);

  @Override
  public void removeLayer(String layer) {
    layers.remove(layer);
  }

  @Override
  public void addLayer(String layer) {
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
