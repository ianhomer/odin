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

import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.properties.thing.ThingCopy;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable performance.
 */
@Data
@Entity(name = "Performance")
@Table(name = "Performance")
@EqualsAndHashCode(of = "id")
@Slf4j
public class PersistablePerformance implements Performance {
  @Id
  @GeneratedValue
  private long id;

  @Column(unique = true)
  private String name;

  @OneToMany(targetEntity = PersistableChannel.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<Channel> channels = new HashSet<>();

  @OneToMany(targetEntity = PersistableLayer.class, cascade = CascadeType.ALL,
      fetch = FetchType.LAZY, mappedBy = "performance", orphanRemoval = true)
  private Set<Layer> layers = new HashSet<>();

  @OneToMany(targetEntity = PersistableTrigger.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<TriggerConfiguration> triggers = new HashSet<>();

  @OneToMany(targetEntity = PersistableSequence.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<SequenceConfiguration> sequences = new HashSet<>();

  @Override
  public PersistablePerformance addChannel(Channel channel) {
    /*
     * Replace any existing channel for same channel number.
     */
    channels.removeIf(c -> c.getNumber() == channel.getNumber());
    return add(bind(ThingCopy.from(channel).coerce(PersistableChannel.class)), channels);
  }

  @Override
  public PersistablePerformance removeChannel(Channel channel) {
    boolean result = channels.removeIf(c -> c.getId() == channel.getId());
    if (!result) {
      LOG.warn("Could not remove channel {} from performance", channel);
    } else {
      LOG.debug("Removed channel from performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance addLayer(Layer layer) {
    return add(bind(ThingCopy.from(layer).coerce(PersistableLayer.class)), layers);
  }

  @Override
  public PersistablePerformance removeLayer(Layer layer) {
    boolean result = layers.removeIf(l -> l.getId() == layer.getId());
    if (!result) {
      LOG.warn("Could not remove layer {} from performance with layers {}", layer, getLayers());
    } else {
      LOG.debug("Removed layer {} from performance which now has layers = {}  ",
          layer, getLayers());
    }
    return this;
  }

  @Override
  public PersistablePerformance addTrigger(TriggerConfiguration trigger) {
    return add(bind(ThingCopy.from(trigger).coerce(PersistableTrigger.class)), triggers);
  }

  @Override
  public PersistablePerformance removeTrigger(TriggerConfiguration trigger) {
    boolean result = triggers.removeIf(t -> t.getId() == trigger.getId());
    if (!result) {
      LOG.warn("Could not remove trigger {} from performance with triggers {}",
          trigger, getTriggers());
    } else {
      LOG.debug("Removed trigger {} from performance which now has layers = {}  ",
          trigger, getTriggers());
    }
    return this;
  }

  @Override
  public PersistablePerformance addSequence(SequenceConfiguration sequence) {
    return add(bind(ThingCopy.from(sequence).coerce(PersistableSequence.class)), sequences);
  }

  @Override
  public PersistablePerformance removeSequence(SequenceConfiguration sequence) {
    boolean result = sequences.removeIf(s -> s.getId() == sequence.getId());
    if (!result) {
      LOG.warn("Could not remove sequence {} from performance with sequences {}",
          sequence, getSequences());
    } else {
      LOG.debug("Removed sequence from performance");
    }
    return this;
  }

  private <T extends Thing> PersistablePerformance add(T thing, Set<T> things) {
    boolean result = things.add(thing);
    if (!result) {
      LOG.warn("Could not add {} to performance", thing);
    } else {
      LOG.debug("Added {} to performance", thing);
    }
    return this;
  }

  private <T extends PerformanceBound> T bind(T thing) {
    thing.setPerformance(this);
    return thing;
  }
}
