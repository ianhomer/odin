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

package com.purplepip.odin.sequencer;

import static com.purplepip.odin.clock.BeatClock.newBeatClock;
import static com.purplepip.odin.clock.measure.StaticBeatMeasureProvider.newMeasureProvider;
import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.creation.sequence.SequenceFactory.newNoteSequenceFactory;
import static com.purplepip.odin.creation.triggers.TriggerFactory.newTriggerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.conductor.LayerConductor;
import com.purplepip.odin.creation.conductor.MutableConductors;
import com.purplepip.odin.creation.reactors.MutableReactors;
import com.purplepip.odin.creation.reactors.TriggerReactor;
import com.purplepip.odin.creation.sequence.SequenceFactory;
import com.purplepip.odin.creation.track.MutableTracks;
import com.purplepip.odin.creation.triggers.Action;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.roll.SequenceRollTrack;
import org.junit.Test;

public class ReactorReceiverTest {
  private TriggerFactory triggerFactory = newTriggerFactory();
  private SequenceFactory<Note> sequenceFactory = newNoteSequenceFactory();
  private BeatClock clock = newBeatClock(120);
  private MeasureProvider measureProvider = newMeasureProvider(4);

  @Test
  public void testSend() throws Exception {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder
        .withName("trigger1").withNote(60).addNoteTrigger()
        .withName("trigger2").withNote(61).addNoteTrigger()
        .addLayer("layer1")
        .withName("track1").withLayers("layer1").withEnabled(false)
        .withTrigger("trigger1", Action.ENABLE)
        .withTrigger("trigger2", Action.DISABLE)
        .addPattern(BEAT, 7);
    MutableConductors conductors = new MutableConductors();
    conductors.refresh(
        project.getLayers().stream(),
        () -> new LayerConductor(clock));
    MutableTracks tracks = new MutableTracks();
    tracks.refresh(
        project.getSequences().stream(),
        () -> new SequenceRollTrack(clock, measureProvider, sequenceFactory),
        conductors);
    MutableReactors reactors = new MutableReactors();
    reactors.refresh(
        project.getTriggers().stream(),
        () -> new TriggerReactor(triggerFactory));
    reactors.bind(conductors, tracks);
    MetricRegistry metricRegistry = new MetricRegistry();
    ReactorReceiver receiver = new ReactorReceiver(reactors, metricRegistry);
    assertFalse(((SequenceRollTrack) tracks.findByName("track1")).getSequenceRoll().isEnabled());

    /*
     * Send note on operation to trigger enabling track
     */
    receiver.send(new NoteOnOperation(0,60,50), -1);
    assertEquals(1, metricRegistry.meter("receiver.triggered").getCount());
    assertTrue(tracks.findByName("track1").isEnabled());

    /*
     * Send note on operation to trigger disabling track
     */
    receiver.send(new NoteOnOperation(0,61, 50), -1);
    assertEquals(2, metricRegistry.meter("receiver.triggered").getCount());
    assertFalse(tracks.findByName("track1").isEnabled());
  }
}