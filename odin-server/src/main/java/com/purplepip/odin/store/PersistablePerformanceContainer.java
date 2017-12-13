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

package com.purplepip.odin.store;

import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.MutableLayer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.server.rest.repositories.ChannelRepository;
import com.purplepip.odin.server.rest.repositories.LayerRepository;
import com.purplepip.odin.server.rest.repositories.SequenceRepository;
import com.purplepip.odin.store.domain.PersistableChannel;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistableSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersistablePerformanceContainer extends PerformanceContainer {
  @Autowired
  private LayerRepository layerRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private SequenceRepository sequenceRepository;

  @Override
  public void addChannel(Channel channel) {
    if (channel instanceof PersistableChannel) {
      ((PersistableChannel) channel).setPerformance(getPerformance());
      channelRepository.save((PersistableChannel) channel);
    }
    super.addChannel(channel);
  }

  @Override
  public PersistablePerformanceContainer addSequence(SequenceConfiguration sequence) {
    LOG.debug("Adding sequence : {}", sequence);
    if (sequence instanceof PersistableSequence) {
      PersistableSequence persistableSequence = ((PersistableSequence) sequence);
      LOG.debug("Saving persistable sequence : {}", sequence);
      persistableSequence.setPerformance(getPerformance());
      sequenceRepository.save((PersistableSequence) sequence);
    }
    super.addSequence(sequence);
    return this;
  }

  @Override
  public PersistablePerformanceContainer addLayer(MutableLayer layer) {
    if (layer instanceof PersistableLayer) {
      ((PersistableLayer) layer).setPerformance(getPerformance());
      layerRepository.save((PersistableLayer) layer);
    }
    super.addLayer(layer);
    return this;
  }
  // TODO : Create addTrigger implementation
}
