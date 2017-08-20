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

package com.purplepip.odin.store;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.MutableLayer;
import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.server.rest.repositories.ChannelRepository;
import com.purplepip.odin.server.rest.repositories.LayerRepository;
import com.purplepip.odin.server.rest.repositories.SequenceRepository;
import com.purplepip.odin.store.domain.PersistableChannel;
import com.purplepip.odin.store.domain.PersistableLayer;
import com.purplepip.odin.store.domain.PersistableSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistableProjectContainer extends ProjectContainer {
  @Autowired
  private LayerRepository layerRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private SequenceRepository sequenceRepository;

  @Override
  public void addChannel(Channel channel) {
    if (channel instanceof PersistableChannel) {
      channel.setProject(getProject());
      channelRepository.save((PersistableChannel) channel);
    }
    super.addChannel(channel);
  }

  @Override
  public void addSequence(Sequence sequence) {
    if (sequence instanceof PersistableSequence) {
      sequence.setProject(getProject());
      sequenceRepository.save((PersistableSequence) sequence);
    }
    super.addSequence(sequence);
  }

  @Override
  public void addLayer(MutableLayer layer) {
    if (layer instanceof PersistableLayer) {
      layer.setProject(getProject());
      layerRepository.save((PersistableLayer) layer);
    }
    super.addLayer(layer);
  }
}
