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

import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.layer.MutableLayer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import com.purplepip.odin.performance.Performance;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
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
  private String name;

  @OneToMany(targetEntity = PersistableChannel.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<Channel> channels = new HashSet<>();

  @OneToMany(targetEntity = PersistableLayer.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<Layer> layers = new HashSet<>();

  @OneToMany(targetEntity = PersistableTrigger.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<TriggerConfiguration> triggers = new HashSet<>();

  @OneToMany(targetEntity = PersistableSequence.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "performance", orphanRemoval = true)
  private Set<SequenceConfiguration> sequences = new HashSet<>();

  @Override
  public PersistablePerformance addChannel(Channel channel) {
    if (channel instanceof PersistableChannel) {
      ((PersistableChannel) channel).setPerformance(this);
    }
    /*
     * Replace any existing channel for same channel number.
     */
    channels.removeIf(c -> c.getNumber() == channel.getNumber());
    boolean result = channels.add(channel);
    if (!result) {
      LOG.warn("Cannot add channel {}", channel);
    }
    return this;
  }

  @Override
  public PersistablePerformance removeChannel(Channel channel) {
    boolean result = channels.remove(channel);
    if (!result) {
      LOG.warn("Could not remove channel {} from performance", channel);
    } else {
      LOG.debug("Removed channel from performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance addSequence(SequenceConfiguration sequence) {
    if (sequence instanceof PersistableSequence) {
      ((PersistableSequence) sequence).setPerformance(this);
    }
    boolean result = sequences.add(sequence);
    if (!result) {
      LOG.warn("Could not add sequence {} to performance", sequence);
    } else {
      LOG.debug("Added sequence to performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance removeSequence(SequenceConfiguration sequence) {
    boolean result = sequences.remove(sequence);
    if (!result) {
      LOG.warn("Could not remove sequence {} from performance", sequence);
    } else {
      LOG.debug("Removed sequence from performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance addLayer(MutableLayer layer) {
    if (layer instanceof PersistableLayer) {
      ((PersistableLayer) layer).setPerformance(this);
    }
    boolean result = layers.add(layer);
    if (!result) {
      LOG.warn("Could not add layer {} to performance", layer);
    } else {
      LOG.debug("Added layer to performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance removeLayer(Layer layer) {
    boolean result = layers.remove(layer);
    if (!result) {
      LOG.warn("Could not remove layer {} from performance with layers {}", layer, getLayers());
    } else {
      LOG.debug("Removed layer {} from performance which now has layers = {}  ",
          layer, getLayers());
    }
    return this;
  }

  @Override
  public PersistablePerformance addTrigger(MutableTriggerConfiguration trigger) {
    if (trigger instanceof PersistableTrigger) {
      ((PersistableTrigger) trigger).setPerformance(this);
    }
    boolean result = triggers.add(trigger);
    if (!result) {
      LOG.warn("Could not add trigger {} to performance", trigger);
    } else {
      LOG.debug("Added trigger to performance");
    }
    return this;
  }

  @Override
  public PersistablePerformance removeTrigger(TriggerConfiguration trigger) {
    boolean result = triggers.remove(trigger);
    if (!result) {
      LOG.warn("Could not remove trigger {} from performance with triggers {}",
          trigger, getTriggers());
    } else {
      LOG.debug("Removed trigger {} from performance which now has layers = {}  ",
          trigger, getTriggers());
    }
    return this;
  }
}
