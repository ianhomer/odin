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

import com.purplepip.odin.clock.tick.Ticks
import com.purplepip.odin.creation.channel.DefaultChannel
import com.purplepip.odin.creation.layer.DefaultLayer
import com.purplepip.odin.music.notes.Notes.newNote
import com.purplepip.odin.music.sequence.Pattern
import com.purplepip.odin.performance.TransientPerformance

class KotlinPerformance : TransientPerformance() {
  init {
    performance(this).apply {
      add(DefaultChannel(9).programName("Power Drums"))
      add(DefaultLayer("performance"))
      add(Pattern().apply {
        name = "beat1" ; channel = 9
        bits(15) ; offset(4)
        layer("performance")
      })
      add(Pattern().bits(15).note(newNote(62))
          .layer("performance")
          .tick(Ticks.THIRD).channel(9).name("beat2"))
      add(Pattern().bits(3).note(newNote(46))
          .offset(4).layer("performance")
          .tick(Ticks.BEAT).channel(9).name("beat3"))

      layer("performance").apply {
        channel(9).apply {
          add(Pattern().apply {
            bits(31)
            name = "beat5"
            tick = Ticks.HALF
            note(newNote(68))
          })
          add(Pattern().apply {
            name = "beat4"
            bits(2)
            note(newNote(45))
            offset(4)
            tick(Ticks.TWO_BEAT)
          })
        }
      }
    }
  }
}