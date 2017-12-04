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

package com.purplepip.odin.demo;

import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.EIGHTH;
import static com.purplepip.odin.clock.tick.Ticks.TWO_THIRDS;
import static com.purplepip.odin.creation.channel.DefaultChannel.newChannel;
import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.TransientPerformance;

/**
 * Performance scratch pad.  This performance can get changed at any time to experiment
 * with features.
 */
public class ScratchPerformance extends TransientPerformance {
  /**
   * Create performance.
   */
  public ScratchPerformance() {
    this
        .addChannel(newChannel(1).programName("rock"))
        .addChannel(newChannel(2).programName("aahs"))
        .addChannel(newChannel(3).programName("bass"))
        .addChannel(newChannel(9).programName("TR-909"))
        .addLayer(newLayer("groove"))
        .addLayer(newLayer("start").offset(0).length(12))
        .addLayer(newLayer("mid").offset(8).length(8))
        .addSequence(new Metronome().channel(9).layer("groove").length(-1).offset(0))
        .addSequence(new Pattern().note(newNote(50, 50))
            .bits(7).channel(3).enabled(true).tick(BEAT)
            .name("pattern-groove").layer("groove"))
        .addSequence(new Pattern().note(newNote(42, 20))
            .bits(15).channel(2).tick(BEAT).layer("start"))
        .addSequence(new Pattern().note(newNote(62, 100))
            .bits(2).channel(9).tick(BEAT).layer("groove"))
        .addSequence(new Pattern().note(newNote(60, 50))
            .bits(127).channel(9).tick(EIGHTH).layer("groove"))
        .addSequence(new Pattern().note(newNote(46))
            .bits(7).channel(9).tick(TWO_THIRDS).layer("groove"))
        .addSequence(new Notation().notation("B4/8, B4, E4/q, G4, C4")
            .channel(3).tick(BEAT).layer("mid"));
  }
}
