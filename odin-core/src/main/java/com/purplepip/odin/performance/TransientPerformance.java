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

package com.purplepip.odin.performance;

import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.ToString;

/**
 * A light weight non-persistable in memory performance implementation.
 */
@ToString(exclude = {"channels", "layers", "sequences"})
public class TransientPerformance implements Performance {
  private static final String TRANSIENT_PERFORMANCE_NAME = "transient";

  private final String name;

  private final URI uri;

  private Set<SequenceConfiguration> sequences = new LinkedHashSet<>();

  private Set<Channel> channels = new LinkedHashSet<>();

  private Set<Layer> layers = new LinkedHashSet<>();

  private Set<TriggerConfiguration> triggers = new LinkedHashSet<>();

  private <T extends Thing> void removeNamedDuplicate(T thing, Set<T> set) {
    if (thing.getName() != null) {
      set.removeIf(existing -> thing.getName().equals(existing.getName()));
    }
  }

  /**
   * Note that if the transient performance has been extended then the
   * name of the performance is named after the class, however otherwise this name is fixed as
   * transient.
   */
  public TransientPerformance() {
    if (getClass().equals(TransientPerformance.class)) {
      name = TRANSIENT_PERFORMANCE_NAME;
      uri = URI.create(name);
    } else {
      uri = new ClassUri(getClass()).getUri();
      name = uri.toString();
    }
  }

  public TransientPerformance(String name) {
    this.name = name;
    this.uri = URI.create(name);
  }

  @Override
  public String getName() {
    return name;
  }

  public URI getUri() {
    return uri;
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
  public TransientPerformance addChannel(Channel channel) {
    channels.add(channel);
    return this;
  }

  @Override
  public TransientPerformance removeChannel(Channel channel) {
    channels.remove(channel);
    return this;
  }

  @Override
  public TransientPerformance addLayer(Layer layer) {
    removeNamedDuplicate(layer, layers);
    layers.add(layer);
    return this;
  }

  @Override
  public TransientPerformance removeLayer(Layer layer) {
    layers.remove(layer);
    return this;
  }

  @Override
  public TransientPerformance addTrigger(TriggerConfiguration trigger) {
    removeNamedDuplicate(trigger, triggers);
    triggers.add(trigger);
    return this;
  }

  @Override
  public TransientPerformance removeTrigger(TriggerConfiguration trigger) {
    triggers.remove(trigger);
    return this;
  }

  @Override
  public TransientPerformance addSequence(SequenceConfiguration sequence) {
    removeNamedDuplicate(sequence, sequences);
    sequences.add(sequence);
    return this;
  }

  @Override
  public TransientPerformance removeSequence(SequenceConfiguration sequence) {
    sequences.remove(sequence);
    return this;
  }
}
