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
import com.purplepip.odin.sequence.TimeUnit;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.layer.MutableLayer;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Persistable layer.
 */
@Data
@Entity(name = "Layer")
@Table(name = "Layer")
@EqualsAndHashCode(exclude = "project")
@ToString(exclude = "project")
public class PersistableLayer implements MutableLayer {
  @Id
  @GeneratedValue
  private long id;
  private String name;

  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @OneToMany(targetEntity = PersistableLayer.class, fetch = FetchType.EAGER)
  private Set<Layer> parents;

  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(targetEntity = PersistableTick.class, cascade = CascadeType.ALL, orphanRemoval = true)
  @NotNull
  private Tick tick;

  @PrePersist
  public void prePesist() {
    addToProject();
    setDefaults();
  }

  public void addToProject() {
    project.addLayer(this);
  }

  public void setDefaults() {
    if (tick == null) {
      PersistableTick newTick = new PersistableTick();
      newTick.setTimeUnit(TimeUnit.BEAT);
      newTick.setNumerator(1);
      newTick.setDenominator(1);
      tick = newTick;
    }
  }

  @PreRemove
  public void removeFromProject() {
    project.removeLayer(this);
  }
}
