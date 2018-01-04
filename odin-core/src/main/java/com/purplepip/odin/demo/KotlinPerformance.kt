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
import com.purplepip.odin.music.notes.Notes.newNote
import com.purplepip.odin.music.sequence.Pattern
import com.purplepip.odin.performance.StaticPerformance

class KotlinPerformance : StaticPerformance(performance().apply {
  layer("performance") {
    channel(1, "Strings") {
      this play "A/q G/8 A/q E"
    }
    channel(2, "Violin") {
      this play "A5/q A/8 A/q G"
    }
    channel(3, "bass") {
      (this play 31).apply { note(newNote(48, 80)) } at Ticks.QUARTER
    }
    channel(4, "aah") {
      this play 1
    }
    channel(9, "Power Drums") {
      (this play 15).apply { note(newNote(62)) ; tick = Ticks.THIRD }
      this play Pattern().apply { bits(3); note(newNote(46))
                          offset(4) ; tick = Ticks.BEAT
      }
      + Pattern().apply { bits(15); offset(4) }
      + Pattern().apply { bits(31); note(newNote(68)) ; tick = Ticks.EIGHTH }
      + Pattern().apply { bits(2); note(newNote(45))
                          offset(4) ; tick = Ticks.TWO_BEAT
      }
    }
  }
}.performance)
