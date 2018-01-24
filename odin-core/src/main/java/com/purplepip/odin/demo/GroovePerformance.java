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
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.QUARTER;
import static com.purplepip.odin.clock.tick.Ticks.TWO_BEAT;
import static com.purplepip.odin.creation.channel.DefaultChannel.newChannel;
import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.TransientPerformance;

/**
 * A demo groove.
 */
public class GroovePerformance extends TransientPerformance {
  private static final String BREAK = "break";
  private static final String INTRO = "intro";
  private static final String VERSE = "verse";

  /**
   * Create performance.
   */
  // TODO : Once we have strong assertion of performance output in GroovePerformanceTest,
  // work on improving the semantics of this performance generation, to be simpler & clearer
  public GroovePerformance() {
    this
        .addChannel(newChannel("piano", 1))
        .addChannel(newChannel("rock", 2))
        .addChannel(newChannel("strings", 3))
        .addChannel(newChannel("aahs", 4))
        .addChannel(newChannel("bass", 5))
        .addChannel(newChannel("Power Drums",9))
        .addLayer(newLayer("overlay"))
        .addLayer(newLayer(INTRO).length(16))
        .addLayer(newLayer(BREAK).length(16))
        .addLayer(newLayer("out").length(4))
        .addLayer(newLayer("in").length(4))
        .addLayer(newLayer("a").length(4))
        .addLayer(newLayer("b").length(4))
        .addLayer(newLayer("c").length(8))
        .addLayer(newLayer(VERSE).layer("a", "b", "c").length(48))
        .addLayer(newLayer("groove")
            .layer("in", INTRO, VERSE, BREAK, "out").length(-1))

        .addSequence(new Notation().notation("A/q G/8 A/q E")
            .channel(1).layer("a", "c").tick(BEAT).name("piano-a"))
        .addSequence(new Notation().notation("C5/q A4/8 C5/q D")
            .channel(1).layer("b").tick(BEAT).name("piano-c"))
        .addSequence(new Notation().notation("A3 A A/8 A A/q")
            .channel(1).layer("a").tick(BEAT).name("piano-a3"))
        .addSequence(new Notation().notation("C3 C C/8 C C/q")
            .channel(1).layer("b").tick(BEAT).name("piano-c3"))
        .addSequence(new Notation().notation("G3 G G/8 A C/q G2 G G3/8 G G/q")
            .channel(1).layer("c").tick(BEAT).name("piano-g3"))
        .addSequence(new Notation().notation("C5/h C/q")
            .channel(1).layer("a").tick(BEAT).name("notes-c5"))
        .addSequence(new Notation().notation("C4/h C/q")
            .channel(1).layer("b").tick(BEAT).name("notes-c4"))
        .addSequence(new Notation().notation("C5/h C/q C6 A5 A5/8 C5/8 A5")
            .channel(1).layer(BREAK).tick(BEAT).name("notes"))

        .addSequence(new Notation().notation("A3 A A/8 A A/q")
            .channel(5).layer("a").tick(BEAT).name("bass-3"))

        .addSequence(new Notation().notation("C5/h C/q")
            .channel(3).layer(INTRO).tick(BEAT).name("strings-c5"))
        .addSequence(new Notation().notation("C4/h C/q")
            .channel(3).layer(INTRO).tick(BEAT).name("strings-c4"))
        .addSequence(new Notation().notation("C5/h C/q C6 A5")
            .channel(3).layer(BREAK).tick(BEAT).name("strings"))

        .addSequence(new Notation().notation("A5/8")
            .channel(2).layer("a", "in").tick(BEAT).name("organ-a"))
        .addSequence(new Notation().notation("G5/q")
            .channel(2).layer("b").tick(BEAT).name("organ-b"))
        .addSequence(new Notation().notation("G/q C C/8 A/8 G/q C D/8 A/8")
            .channel(2).layer("c").tick(BEAT).name("organ-c"))

        .addSequence(new Notation().notation("C A C5/h C5/8")
            .channel(4).layer("a").tick(BEAT).name("aahs-a"))
        .addSequence(new Notation().notation("C5/h A4 G4 E C")
            .channel(4).layer("c").tick(TWO_BEAT).name("aahs-c"))
        .addSequence(new Notation().notation("C5/h A4 G4 E C")
            .channel(4).layer(BREAK).tick(BEAT).name("aahs-br"))

        .addSequence(new Pattern().bits(15).note(newNote(69))
            .channel(9).layer("overlay").tick(BEAT).name("shake"))
        .addSequence(new Pattern().bits(13).note(newNote(33))
            .channel(9).layer("a", "b").tick(HALF).name("kick1"))
        .addSequence(new Pattern().bits(65).note(newNote(33))
            .channel(9).layer("c").tick(QUARTER).name("kick2"))
        .addSequence(new Pattern().bits(87).note(newNote(33))
            .channel(9).layer(BREAK).tick(QUARTER).name("kick3"))
        .addSequence(new Pattern().bits(15).note(newNote(42))
            .channel(9).layer(VERSE).tick(HALF).name("hi"))
        .addSequence(new Pattern().bits(87).note(newNote(46))
            .channel(9).layer("c").tick(HALF).name("crash"));
  }
}
