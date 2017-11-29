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

package com.purplepip.odin.creation.reactors;

import static com.purplepip.odin.configuration.TriggerFactories.newTriggerFactory;
import static com.purplepip.odin.music.notes.Notes.newNote;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.track.SequenceTracks;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.performance.TransientPerformance;
import org.junit.Test;

public class MutableReactorsTest {
  private TriggerFactory triggerFactory = newTriggerFactory();

  @Test
  public void testRefresh() {
    TransientPerformance project = new TransientPerformance();
    project.addTrigger(new NoteTrigger().note(newNote(60)).name("trigger"));

    LayerConductors conductors = new LayerConductors();
    TriggerReactors reactors = new TriggerReactors(new SequenceTracks(conductors),
        new LayerConductors());
    reactors.refresh(
        project.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, triggerFactory));

    assertEquals(1, reactors.getStatistics().getAddedCount());
  }

  @Test
  public void testInjection() {
    TransientPerformance project = new TransientPerformance();
    project.addSequence(new Random().range(60, 72).bits(1).name("random"));
    project.addTrigger(new PatternNoteTrigger().patternName("random").name("trigger"));
    LayerConductors conductors = new LayerConductors();
    SequenceTracks tracks = new SequenceTracks(conductors);
    TriggerReactors reactors = new TriggerReactors(tracks, conductors);
    reactors.refresh(project.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, triggerFactory));
    // TODO : Complete test.
  }
}