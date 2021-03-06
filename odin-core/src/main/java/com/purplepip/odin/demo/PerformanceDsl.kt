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
import com.purplepip.odin.creation.layer.MutableLayer
import com.purplepip.odin.creation.sequence.SequenceConfiguration
import com.purplepip.odin.creation.sequence.SequencePlugin
import com.purplepip.odin.creation.triggers.MutableTriggerConfiguration
import com.purplepip.odin.music.notes.Notes
import com.purplepip.odin.music.sequence.Pattern
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

infix fun SequencePlugin.at(value: Tick) : SequencePlugin {
  tick = value
  return this
}

operator fun SequencePlugin.plus(offset: Long) : SequencePlugin {
  offset(offset)
  return this
}

operator fun Pattern.invoke(note: Int) : Pattern {
  note(Notes.newNote(note))
  return this
}

operator fun Pattern.invoke(note: Int, velocity : Int) : Pattern {
  note(Notes.newNote(note, velocity))
  return this
}