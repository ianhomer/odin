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
import com.purplepip.odin.music.notes.Notes.newNote
import com.purplepip.odin.music.sequence.Pattern

class KotlinPerformance {
  var context: PerformanceConfigurationContext = performance().apply {
    add(DefaultChannel(9).programName("Power Drums"))
    layer("performance") {
      channel(9) {
        this add Pattern().apply {
          name = "beat2"
          bits(15) ; note(newNote(62))
          tick = Ticks.THIRD
        }
        this add Pattern().apply {
          name = "beat3"
          bits(3) ; note(newNote(46))
          offset(4)
          tick = Ticks.BEAT
        }
        this add Pattern().apply {
          name = "beat1"
          bits(15); offset(4)
        }
        this add Pattern().apply {
          name = "beat5"
          bits(31); note(newNote(68))
          tick = Ticks.EIGHTH
        }
        this add Pattern().apply {
          name = "beat4"
          bits(2); note(newNote(45))
          tick = Ticks.TWO_BEAT
          offset(4)
        }
      }
    }
  }
}