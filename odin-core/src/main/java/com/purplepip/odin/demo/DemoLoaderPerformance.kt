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

import com.purplepip.odin.creation.action.LoadAction
import com.purplepip.odin.creation.triggers.NoteTrigger
import com.purplepip.odin.music.sequence.Loader
import com.purplepip.odin.performance.StaticPerformance

class DemoLoaderPerformance : StaticPerformance(performance().apply {
  add(NoteTrigger().apply { note(50) ; name("note-50-trigger") })
  layer("loader") {
    play(Loader().apply {
      name("loader")
      enabled(false)
      offset(4)
      length(4)
      trigger("note-50-trigger",
          LoadAction().performance("com.purplepip.odin.demo.SimplePerformance")
      )
    })
    /*
     * Currently need click track for tests so that we know that sequence has started.  In the
     * future we'll create a way for external caller to know that sequencer has started / clock is
     * is +ve.
     */
    channel(9, "Power Drums") {
      play(31)
    }
  }
}.performance)