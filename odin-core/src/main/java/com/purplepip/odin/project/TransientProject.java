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

package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.sequence.MutableLayer;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashSet;
import java.util.Set;

/**
 * A light weight non-persistable in memory project implementation.
 */
public class TransientProject implements Project {
  private static final String TRANSIENT_PROJECT_NAME = "transient";

  private Set<Sequence> sequences = new HashSet<>();

  private Set<Channel> channels = new HashSet<>();

  private Set<Layer> layers = new HashSet<>();

  @Override
  public String getName() {
    return TRANSIENT_PROJECT_NAME;
  }

  @Override
  public Set<Sequence> getSequences() {
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
  public void addSequence(Sequence sequence) {
    sequence.setProject(this);
    sequences.add(sequence);
  }

  @Override
  public void removeSequence(Sequence sequence) {
    sequences.remove(sequence);
  }
}
