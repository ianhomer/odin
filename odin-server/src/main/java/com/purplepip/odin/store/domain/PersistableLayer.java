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

import com.purplepip.odin.creation.layer.MutableLayer;
import com.purplepip.odin.performance.Performance;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable layer.
 */
@Data
@Entity(name = "Layer")
@Table(name = "Layer")
@ToString(exclude = "performance", callSuper = true)
@EqualsAndHashCode(exclude = {"performance", "layers"}, callSuper = true)
@Slf4j
public class PersistableLayer extends PersistableTimeThing implements MutableLayer {
  @ManyToOne(targetEntity = PersistablePerformance.class)
  @JoinColumn(name = "PERFORMANCE_ID", nullable = false)
  private Performance performance;

  @ElementCollection
  private List<String> layers = new ArrayList<>(0);

  @PrePersist
  public void prePesist() {
    addToPerformance();
  }

  @PreUpdate
  public void preUpdate() {
    PersistableHelper.removeDuplicates(layers);
  }

  private void addToPerformance() {
    performance.addLayer(this);
  }

  @PreRemove
  public void removeFromPerformance() {
    performance.removeLayer(this);
  }

  @Override
  public void addLayer(String layer) {
    LOG.debug("Adding layer {} to {}", layer, this);
    layers.add(layer);
  }
}
