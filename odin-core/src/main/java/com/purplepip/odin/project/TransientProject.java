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

package com.purplepip.odin.project;

import com.purplepip.odin.composition.layer.Layer;
import com.purplepip.odin.composition.layer.MutableLayer;
import com.purplepip.odin.composition.sequence.SequenceConfiguration;
import com.purplepip.odin.composition.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.composition.triggers.TriggerConfiguration;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.ToString;

/**
 * A light weight non-persistable in memory project implementation.
 */
@ToString(exclude = {"channels", "layers", "sequences"})
public class TransientProject implements Project {
  private static final String TRANSIENT_PROJECT_NAME = "transient";

  private Set<SequenceConfiguration> sequences = new HashSet<>();

  private Set<Channel> channels = new HashSet<>();

  private Set<Layer> layers = new LinkedHashSet<>();

  private Set<TriggerConfiguration> triggers = new LinkedHashSet<>();

  @Override
  public String getName() {
    return TRANSIENT_PROJECT_NAME;
  }

  @Override
  public Set<SequenceConfiguration> getSequences() {
    return sequences;
  }

  @Override
  public Set<Channel> getChannels() {
    return channels;
  }

  @Override
  public Set<Layer> getLayers() {
    return layers;
  }

  @Override
  public Set<TriggerConfiguration> getTriggers() {
    return triggers;
  }

  @Override
  public void addChannel(Channel channel) {
    channel.setProject(this);
    channels.add(channel);
  }

  @Override
  public void removeChannel(Channel channel) {
    channels.remove(channel);
  }

  @Override
  public void addLayer(MutableLayer layer) {
    layer.setProject(this);
    layers.add(layer);
  }

  @Override
  public void removeLayer(Layer layer) {
    layers.remove(layer);
  }

  @Override
  public void addTrigger(MutableTriggerConfiguration trigger) {
    trigger.setProject(this);
    triggers.add(trigger);
  }

  @Override
  public void removeTrigger(TriggerConfiguration trigger) {
    triggers.remove(trigger);
  }

  @Override
  public void addSequence(SequenceConfiguration sequence) {
    sequence.setProject(this);
    sequences.add(sequence);
  }

  @Override
  public void removeSequence(SequenceConfiguration sequence) {
    sequences.remove(sequence);
  }
}
