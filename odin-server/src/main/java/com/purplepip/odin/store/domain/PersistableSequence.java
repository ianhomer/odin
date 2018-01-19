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

import com.purplepip.odin.creation.action.ActionConfiguration;
import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.properties.thing.ThingCopy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
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
@ToString(exclude = "performance", callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class PersistableSequence  extends PersistableTimeThing
    implements MutableSequenceConfiguration, PerformanceBound {
  @ManyToOne(targetEntity = PersistablePerformance.class)
  @JoinColumn(name = "PERFORMANCE_ID", nullable = false)
  private Performance performance;

  @NotNull
  private String type;

  private int channel;

  @ElementCollection
  private List<String> layers = new ArrayList<>(0);

  @OneToMany(targetEntity = PersistableAction.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "sequence", orphanRemoval = true)
  @MapKey(name = "id")
  private Map<String, ActionConfiguration> triggers = new HashMap<>(0);

  /**
   * Set triggers.
   *
   * @param triggers triggers to set
   */
  public void setTriggers(Map<String, ActionConfiguration> triggers) {
    this.triggers.clear();
    /*
     * Copy triggers across into this object making sure that the actions are PersistableActions.
     */
    triggers.forEach((key, value) ->
        this.triggers.put(key, ThingCopy.from(value).coerce(PersistableAction.class))
    );
  }

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
  public void addTrigger(String trigger, ActionConfiguration action) {
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

  /**
   * Pre-persist.
   */
  @PrePersist
  public void prePesist() {
    addToPerformance();
  }

  public void addToPerformance() {
    performance.addSequence(this);
  }

  @PreRemove
  public void removeFromPerformance() {
    performance.removeSequence(this);
  }

  @Override
  public boolean isEmpty() {
    return getLayers().isEmpty();
  }
}
