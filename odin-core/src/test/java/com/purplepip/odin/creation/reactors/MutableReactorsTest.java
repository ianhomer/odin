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

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.measure.StaticBeatMeasureProvider.newMeasureProvider;
import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static com.purplepip.odin.configuration.TriggerFactories.newTriggerFactory;
import static com.purplepip.odin.music.notes.Notes.newNote;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.track.SequenceRollTrack;
import com.purplepip.odin.creation.track.SequenceTracks;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.performance.TransientPerformance;
import org.junit.jupiter.api.Test;

class MutableReactorsTest {
  private TriggerFactory triggerFactory = newTriggerFactory();
  private ActionFactory actionFactory = newActionFactory();
  private FlowFactory flowFactory = newNoteFlowFactory();
  private BeatClock clock = newPrecisionBeatClock(120);
  private MeasureProvider measureProvider = newMeasureProvider(4);

  @Test
  void testRefresh() {
    TransientPerformance project = new TransientPerformance();
    project.addTrigger(new NoteTrigger().note(newNote(60)).name("trigger"));

    LayerConductors conductors = new LayerConductors();
    TriggerReactors reactors = new TriggerReactors(new SequenceTracks(conductors));
    reactors.refresh(
        project.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, triggerFactory));

    assertEquals(1, reactors.getStatistics().getAddedCount());
  }

  @Test
  void testInjection() {
    TransientPerformance project = new TransientPerformance();
    project.addSequence(new Random().range(60, 72).bits(1).name("random"));
    project.addTrigger(new PatternNoteTrigger().patternName("random").name("trigger"));
    LayerConductors conductors = new LayerConductors();
    SequenceTracks tracks = new SequenceTracks(conductors);
    tracks.refresh(project.getSequences().stream(), sequence ->
        new SequenceRollTrack(sequence, clock, measureProvider, flowFactory, actionFactory));
    TriggerReactors reactors = new TriggerReactors(tracks);
    reactors.refresh(project.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, triggerFactory));
    // TODO : Complete test.
  }
}