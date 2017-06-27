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

package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.sequence.MutableLayer;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
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

/**
 * Persistable project.
 */
@Data
@Entity(name = "Project")
@Table(name = "Project")
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
  private Set<Layer> layers = new HashSet<>();
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
  public void removeLayer(Layer layer) {
    layers.remove(layer);
  }

  @Override
  public void addLayer(MutableLayer layer) {
    layer.setProject(this);
    layers.add(layer);
  }

  @Override
  public void removeChannel(Channel channel) {
    channels.remove(channel);
  }

  @Override
  public void addSequence(Sequence sequence) {
    sequence.setProject(this);
    sequences.add(sequence);
  }

  @Override
  public void removeSequence(Sequence sequence) {
    sequences.remove(sequence);
  }
}
