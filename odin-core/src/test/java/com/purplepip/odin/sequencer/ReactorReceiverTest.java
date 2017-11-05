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

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.MutableSequenceRoll;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.conductor.LayerConductor;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.flow.DefaultFlowConfiguration;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.sequence.reactors.MutableReactors;
import com.purplepip.odin.sequence.reactors.TriggerReactor;
import com.purplepip.odin.sequence.triggers.Action;
import com.purplepip.odin.sequence.triggers.TriggerFactory;
import org.junit.Before;
import org.junit.Test;

public class ReactorReceiverTest {
  private TriggerFactory triggerFactory = TriggerFactory.createTriggerFactory();
  private BeatClock clock;
  private SequenceFactory<Note> sequenceFactory;
  private MeasureProvider measureProvider;

  /**
   * Set up test.
   */
  @Before
  public void setUp() {
    clock = new BeatClock(new StaticBeatsPerMinute(120));
    sequenceFactory =
        SequenceFactory.createNoteSequenceFactory(new DefaultFlowConfiguration());
    measureProvider = new StaticBeatMeasureProvider(4);
  }

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
    conductors.refresh(() -> project.getLayers().stream(), () -> new LayerConductor(clock));
    MutableTracks tracks = new MutableTracks();
    tracks.refresh(() -> project.getSequences().stream(), this::createSequenceTrack, conductors);
    MutableReactors reactors = new MutableReactors();
    reactors.refresh(() -> project.getTriggers().stream(), () -> new TriggerReactor(triggerFactory),
        conductors, tracks);
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

  private SequenceRollTrack createSequenceTrack() {
    return new SequenceRollTrack(clock,
        new MutableSequenceRoll<>(clock, sequenceFactory , measureProvider));
  }
}