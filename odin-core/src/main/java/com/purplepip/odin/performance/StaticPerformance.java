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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.layer.MutableLayer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import java.util.Set;
import lombok.ToString;

@ToString
public class StaticPerformance implements Performance {
  private final Performance performance;

  public StaticPerformance(Performance performance) {
    this.performance = performance;
  }

  @Override
  public String getName() {
    return performance.getName();
  }

  @Override
  public Set<SequenceConfiguration> getSequences() {
    return performance.getSequences();
  }

  @Override
  public Set<Channel> getChannels() {
    return performance.getChannels();
  }

  @Override
  public Set<Layer> getLayers() {
    return performance.getLayers();
  }

  @Override
  public Set<TriggerConfiguration> getTriggers() {
    return performance.getTriggers();
  }

  @Override
  public Performance addChannel(Channel channel) {
    throw new OdinRuntimeException("Cannot add channel to " + this);
  }

  @Override
  public Performance removeChannel(Channel channel) {
    throw new OdinRuntimeException("Cannot remove channel from " + this);
  }

  @Override
  public Performance addLayer(MutableLayer layer) {
    throw new OdinRuntimeException("Cannot add layer to " + this);
  }

  @Override
  public Performance removeLayer(Layer layer) {
    throw new OdinRuntimeException("Cannot remove layer from " + this);
  }

  @Override
  public Performance addTrigger(MutableTriggerConfiguration trigger) {
    throw new OdinRuntimeException("Cannot add trigger to " + this);
  }

  @Override
  public Performance removeTrigger(TriggerConfiguration trigger) {
    throw new OdinRuntimeException("Cannot remove trigger from " + this);
  }

  @Override
  public Performance addSequence(SequenceConfiguration sequence) {
    throw new OdinRuntimeException("Cannot add sequence to " + this);
  }

  @Override
  public Performance removeSequence(SequenceConfiguration sequence) {
    throw new OdinRuntimeException("Cannot remove sequence from " + this);
  }
}
