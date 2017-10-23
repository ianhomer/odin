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

package com.purplepip.odin.sequence;

import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.sequence.tick.MutableTimeThing;
import com.purplepip.odin.sequence.triggers.Action;

/**
 * Mutable sequence.
 */
public interface MutableSequenceConfiguration extends SequenceConfiguration,
    MutableTimeThing, MutablePropertiesProvider {
  void setChannel(int channel);

  void setFlowName(String flowName);

  void addLayer(String layerName);

  void removeLayer(String layerName);

  void addTrigger(String triggerName, Action action);

  void removeTrigger(String triggerName);
}
