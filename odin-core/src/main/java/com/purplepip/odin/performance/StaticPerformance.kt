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

package com.purplepip.odin.performance

import com.purplepip.odin.common.OdinRuntimeException
import com.purplepip.odin.creation.channel.Channel
import com.purplepip.odin.creation.layer.Layer
import com.purplepip.odin.creation.sequence.SequenceConfiguration
import com.purplepip.odin.creation.triggers.TriggerConfiguration

open class StaticPerformance(private val performance: Performance) : Performance {
  constructor(block: PerformanceConfigurationContext.() -> Unit) :
    this(PerformanceConfigurationContext(TransientPerformance()).apply(block).performance)

  override fun getName(): String {
    return performance.name
  }

  override fun getSequences(): Set<SequenceConfiguration> {
    return performance.sequences
  }

  override fun getChannels(): Set<Channel> {
    return performance.channels
  }

  override fun getLayers(): Set<Layer> {
    return performance.layers
  }

  override fun getTriggers(): Set<TriggerConfiguration> {
    return performance.triggers
  }

  override fun addChannel(channel: Channel): Performance {
    throw OdinRuntimeException("Cannot add channel to " + this)
  }

  override fun removeChannel(channel: Channel): Performance {
    throw OdinRuntimeException("Cannot remove channel from " + this)
  }

  override fun addLayer(layer: Layer): Performance {
    throw OdinRuntimeException("Cannot add layer to " + this)
  }

  override fun removeLayer(layer: Layer): Performance {
    throw OdinRuntimeException("Cannot remove layer from " + this)
  }

  override fun addTrigger(trigger: TriggerConfiguration): Performance {
    throw OdinRuntimeException("Cannot add trigger to " + this)
  }

  override fun removeTrigger(trigger: TriggerConfiguration): Performance {
    throw OdinRuntimeException("Cannot remove trigger from " + this)
  }

  override fun addSequence(sequence: SequenceConfiguration): Performance {
    throw OdinRuntimeException("Cannot add sequence to " + this)
  }

  override fun removeSequence(sequence: SequenceConfiguration): Performance {
    throw OdinRuntimeException("Cannot remove sequence from " + this)
  }
}
