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

import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.creation.action.IncrementAction;
import com.purplepip.odin.creation.action.InitialiseAction;
import com.purplepip.odin.creation.action.SetAction;
import com.purplepip.odin.creation.action.StartAction;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.SequenceStartTrigger;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;

/**
 * Play random note and expect incoming note to match the note.  If the note matches the
 * expected note then a success sequence is played and new random note is played.
 */
public class MatchNotePerformance {
  /**
   * Create performance.
   *
   * @return performance container
   */
  public PerformanceContainer create() {
    return new PerformanceContainer(new TransientPerformance())
        .addLayer(newLayer("groove"))
        .addSequence(new Random()
            .lower(60).upper(72)
            .bits(1).note(newNote())
            .trigger("success-start-trigger",
                new SetAction().nameValuePairs("channel=3"),
                new InitialiseAction(),
                new StartAction()
            )
            .length(4)
            .channel(2).layer("groove")
            .name("random"))
        .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger"))
        .addTrigger(new SequenceStartTrigger()
            .sequenceName("success").name("success-start-trigger"))
        .addSequence(new Notation()
            .notation("C D E F")
            .trigger("random-note-trigger",
                new IncrementAction().propertyName("channel").increment(1),
                new StartAction())
            .channel(5).layer("groove")
            .length(4)
            .enabled(false)
            .name("success"));
  }
}
