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

import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import java.util.stream.Stream;

public interface PerformanceContainer {
  Performance getPerformance();

  void setPerformance(Performance performance);

  String getName();

  void save();

  void load();

  void apply();

  void addApplyListener(PerformanceApplyListener listener);

  void addLoadListener(PerformanceLoadListener listener);

  void addSaveListener(PerformanceSaveListener listener);

  SequenceConfiguration getSequence(long id);

  Iterable<SequenceConfiguration> getSequences();

  Stream<SequenceConfiguration> getSequenceStream();

  DefaultPerformanceContainer addSequence(SequenceConfiguration sequence);

  void removeSequence(SequenceConfiguration sequence);

  DefaultPerformanceContainer addChannel(Channel channel);

  Channel getChannel(long id);

  DefaultPerformanceContainer addLayer(Layer layer);

  Layer getLayer(long id);

  Stream<Layer> getLayerStream();

  DefaultPerformanceContainer addTrigger(TriggerConfiguration trigger);

  TriggerConfiguration getTrigger(long id);

  Stream<TriggerConfiguration> getTriggerStream();

  void removeChannel(Channel channel);

  Iterable<Channel> getChannels();

  Stream<Channel> getChannelStream();

  boolean isEmpty();

  void mixin(Performance mixin);
}
