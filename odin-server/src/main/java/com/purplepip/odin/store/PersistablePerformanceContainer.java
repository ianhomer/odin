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

import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.server.rest.repositories.ChannelRepository;
import com.purplepip.odin.server.rest.repositories.LayerRepository;
import com.purplepip.odin.server.rest.repositories.SequenceRepository;
import com.purplepip.odin.server.rest.repositories.TriggerRepository;
import com.purplepip.odin.store.domain.PersistablePerformance;
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

  @Autowired
  private TriggerRepository triggerRepository;

  @Override
  public void setPerformance(Performance performance) {
    if (performance instanceof PersistablePerformance) {
      super.setPerformance(performance);
    } else {
      PersistablePerformance persistablePerformance = new PersistablePerformance();
      persistablePerformance.setName(performance.getName());
      super.setPerformance(persistablePerformance);
      save();
      mixin(performance);
      save();
    }
  }

  @Override
  public void mixin(Performance performance) {
    PersistablePerformance mixin = new PersistablePerformance();
    performance.getChannels().forEach(mixin::addChannel);
    performance.getLayers().forEach(mixin::addLayer);
    performance.getSequences().forEach(mixin::addSequence);
    performance.getTriggers().forEach(mixin::addTrigger);

    mixin.getLayers().forEach(this::addLayer);
    mixin.getChannels().forEach(this::addChannel);
    mixin.getSequences().forEach(this::addSequence);
    mixin.getTriggers().forEach(this::addTrigger);
  }
}
