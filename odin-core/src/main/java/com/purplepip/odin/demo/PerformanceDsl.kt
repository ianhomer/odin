
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

import com.purplepip.odin.clock.tick.Tick
import com.purplepip.odin.creation.channel.Channel
import com.purplepip.odin.creation.channel.DefaultChannel
import com.purplepip.odin.creation.layer.DefaultLayer
import com.purplepip.odin.creation.layer.MutableLayer
import com.purplepip.odin.creation.sequence.GenericSequence
import com.purplepip.odin.creation.sequence.MutableSequenceConfiguration
import com.purplepip.odin.creation.sequence.SequenceConfiguration
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration
import com.purplepip.odin.creation.triggers.Trigger
import com.purplepip.odin.music.notes.Notes.newNote
import com.purplepip.odin.music.sequence.Notation
import com.purplepip.odin.music.sequence.Pattern
import com.purplepip.odin.performance.Performance
import com.purplepip.odin.performance.TransientPerformance

fun TransientPerformance.add(value: Any) {
  when (value) {
    is MutableLayer -> addLayer(value)
    is SequenceConfiguration -> addSequence(value)
    is Channel -> addChannel(value)
    is MutableTriggerConfiguration -> addTrigger(value)
  }
}

infix fun SequenceConfiguration.plus(block: SequenceConfiguration.() -> SequenceConfiguration) {
  apply { block.invoke(this) }
}

infix fun MutableSequenceConfiguration.at(value: Tick) : MutableSequenceConfiguration {
  tick = value
  return this
}

operator fun GenericSequence.plus(offset: Long) : GenericSequence {
  offset(offset)
  return this
}

operator fun Pattern.invoke(note: Int) : Pattern {
  note(newNote(note))
  return this
}

operator fun Pattern.invoke(note: Int, velocity : Int) : Pattern {
  note(newNote(note, velocity))
  return this
}

class PerformanceConfigurationContext constructor(performance: TransientPerformance) {
  var performance : TransientPerformance = performance
  var channel : Int = 0
  var layers : Array<out String> = emptyArray()

  fun channel(channel: Int, instrument: String, init: () -> Unit) : PerformanceConfigurationContext {
    performance.add(DefaultChannel(channel).programName(instrument))
    return channel(channel, init)
  }

  fun channel(channel: Int, init: () -> Unit) : PerformanceConfigurationContext {
    this.channel = channel
    init.invoke()
    return this
  }

  fun mixin(performance: Performance) {
    this.performance.mixin(performance)
  }

  fun layer(vararg names : String, init: () -> Unit) : PerformanceConfigurationContext {
    names.filter { name -> performance.layers.count { layer -> layer.name == name } == 0}
        .forEach { name -> performance.add(DefaultLayer(name))}
    this.layers = names
    init.invoke()
    return this
  }

  operator fun GenericSequence.unaryPlus() {
    play(this)
  }

  infix fun play(bits: Int) : Pattern {
    val sequence = Pattern().apply { bits(bits) }
    play(sequence)
    return sequence
  }

  infix fun play(notation: String) : Notation {
    val sequence = Notation().apply { notation(notation) }
    play(sequence)
    return sequence
  }

  infix fun play(value: GenericSequence) : GenericSequence {
    if (value.channel == 0) value.channel = channel
    if (value.layers.isEmpty()) value.layer(*layers)
    performance.add(value)
    return value
  }

  infix fun add(trigger: Trigger) : Trigger {
    performance.add(trigger)
    return trigger
  }
}

fun performance() : PerformanceConfigurationContext {
  return PerformanceConfigurationContext(TransientPerformance())
}