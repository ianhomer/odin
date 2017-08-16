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
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.layer.MutableLayer;
import com.purplepip.odin.sequencer.Channel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
 * Persistable project.
 */
@Data
@Entity(name = "Project")
@Table(name = "Project")
@EqualsAndHashCode(of = "name")
@Slf4j
public class PersistableProject implements Project {
  @Id
  @GeneratedValue
  private long id;
  private String name;

  @OneToMany(targetEntity = PersistableChannel.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "project", orphanRemoval = true)
  private Set<Channel> channels = new HashSet<>();

  @OneToMany(targetEntity = PersistableLayer.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "project", orphanRemoval = true)
  private List<Layer> layers = new ArrayList<>();

  @OneToMany(targetEntity = AbstractPersistableSequence.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, mappedBy = "project", orphanRemoval = true)
  private Set<Sequence> sequences = new HashSet<>();

  @Override
  public void addChannel(Channel channel) {
    channel.setProject(this);
    /*
     * Replace any existing channel for same channel number.
     */
    channels.removeIf(c -> c.getNumber() == channel.getNumber());
    channels.add(channel);
  }

  @Override
  public void removeChannel(Channel channel) {
    boolean result = channels.remove(channel);
    if (!result) {
      LOG.warn("Could not remove channel {} from project", channel);
    } else {
      LOG.debug("Removed channel from project");
    }
  }

  @Override
  public void addSequence(Sequence sequence) {
    sequence.setProject(this);
    boolean result = sequences.add(sequence);
    if (!result) {
      LOG.warn("Could not add sequence {} to project", sequence);
    } else {
      LOG.debug("Added sequence to project");
    }
  }

  @Override
  public void removeSequence(Sequence sequence) {
    boolean result = sequences.remove(sequence);
    if (!result) {
      LOG.warn("Could not remove sequence {} from project", sequence);
    } else {
      LOG.debug("Removed sequence from project");
    }
  }

  @Override
  public void addLayer(MutableLayer layer) {
    if (!this.equals(layer.getProject())) {
      layer.setProject(this);
    }
    if (!layers.contains(layer)) {
      boolean result = layers.add(layer);
      if (!result) {
        LOG.warn("Could not add layer {} to project", layer);
      } else {
        LOG.debug("Added layer {} to project which now has {} layers", layer, layers.size());
      }
    } else {
      LOG.debug("Since it already exists in project, " +
          "not adding layer {} to project which now has {} layers", layer, layers.size());
    }
  }

  @Override
  public void removeLayer(Layer layer) {
    boolean result = layers.removeIf(existingLayer -> existingLayer.equals(layer));
    if (!result) {
      LOG.warn("Could not remove layer {} from project with layers {}", layer, getLayers());
      /*
       * TODO : What's up these layers in the hash set?  is equals and hashcode implemented
       * OK?  For now we're during a brute force clean, but this needs to be fixed ;)
       */
      Set<Layer> nonMatchingLayers = getLayers().stream()
          .filter(existingLayer -> !existingLayer.equals(layer))
          .collect(Collectors.toSet());
      int matchCount = layers.size() - nonMatchingLayers.size();
      if (matchCount > 0) {
        LOG.info("Found {} matches out of {} - removing by brute force", matchCount,
            layers.size());
        layers.clear();
        layers.addAll(nonMatchingLayers);
        LOG.debug("Layers remaining : {}", layers.size());
      }
    } else {
      LOG.debug("Removed layer {} from project which now has layers = {}  ", layer, getLayers());
    }
  }
}
