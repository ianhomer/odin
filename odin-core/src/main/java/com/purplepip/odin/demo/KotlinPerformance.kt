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
      + Pattern().apply { bits(1) }
    }
    channel(9, "Power Drums") {
      + Pattern().apply {
        name = "beat2"
        bits(15); note(newNote(62))
        tick = Ticks.THIRD
      }
      + Pattern().apply {
        name = "beat3"
        bits(3); note(newNote(46))
        offset(4)
        tick = Ticks.BEAT
      }
      + Pattern().apply {
        name = "beat1"
        bits(15); offset(4)
      }
      + Pattern().apply {
        name = "beat5"
        bits(31); note(newNote(68))
        tick = Ticks.EIGHTH
      }
      + Pattern().apply {
        name = "beat4"
        bits(2); note(newNote(45))
        tick = Ticks.TWO_BEAT
        offset(4)
      }
    }
  }
}.performance)
