
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

package com.purplepip.odin.demo

import com.purplepip.odin.creation.channel.Channel
import com.purplepip.odin.creation.layer.MutableLayer
import com.purplepip.odin.creation.sequence.GenericSequence
import com.purplepip.odin.creation.sequence.SequenceConfiguration
import com.purplepip.odin.performance.TransientPerformance

fun TransientPerformance.add(value: Any) {
  when (value) {
    is MutableLayer -> addLayer(value)
    is SequenceConfiguration -> addSequence(value)
    is Channel -> addChannel(value)
  }
}

class PerformanceConfigurationContext constructor(performance: TransientPerformance) {
  var performance : TransientPerformance = performance
  var channel : Int = 0
  var layer : Array<out String> = emptyArray()

  fun channel(channel: Int) : PerformanceConfigurationContext {
    this.channel = channel
    return this
  }

  fun layer(vararg layer : String) : PerformanceConfigurationContext {
    this.layer = layer
    return this
  }

  fun add(value: Any) {
    when (value) {
      is GenericSequence -> {
        if (value.channel == 0) value.channel = channel
        if (value.layers.isEmpty()) value.layer(*layer)
      }
    }
    performance.add(value)
  }
}

fun performance(performance : TransientPerformance) : PerformanceConfigurationContext {
  return PerformanceConfigurationContext(performance)
}