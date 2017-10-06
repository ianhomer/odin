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

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.layer.MutableLayer;
import com.purplepip.odin.sequence.triggers.MutableTrigger;
import com.purplepip.odin.sequence.triggers.Trigger;
import com.purplepip.odin.sequencer.Channel;
import java.util.Set;

/**
 * An Odin Project that stores configuration and state of the given runtime.
 */
public interface Project {
  String getName();

  /**
   * Get set of the sequences stored in this project.
   *
   * @return set of sequences
   */
  Set<Sequence> getSequences();

  /**
   * Get set of the channels stored in this project.
   *
   * @return set of channels
   */
  Set<Channel> getChannels();

  /**
   * Get set of the layers stored in this project.
   *
   * @return set of layers
   */
  Set<Layer> getLayers();

  /**
   * Get set of the triggers stored in this project.
   *
   * @return set of layers
   */
  Set<Trigger> getTriggers();

  /**
   * Add channel.
   */
  void addChannel(Channel channel);

  /**
   * Remove channel.
   */
  void removeChannel(Channel channel);

  /**
   * Add layer.
   */
  void addLayer(MutableLayer layer);

  /**
   * Remove layer.
   */
  void removeLayer(Layer layer);

  /**
   * Add trigger.
   */
  void addTrigger(MutableTrigger trigger);

  /**
   * Remove trigger.
   */
  void removeTrigger(Trigger trigger);

  /**
   * Add sequence.
   */
  void addSequence(Sequence sequence);

  /**
   * Remove channel.
   */
  void removeSequence(Sequence sequence);

  /**
   * Clear the project's content.
   */
  default void clear() {
    getLayers().clear();
    getSequences().clear();
    getChannels().clear();
  }
}
