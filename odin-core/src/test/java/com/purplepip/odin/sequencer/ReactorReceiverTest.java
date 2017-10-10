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
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.MutableSequenceRoll;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.conductor.Conductor;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.flow.DefaultFlowConfiguration;
import com.purplepip.odin.sequence.flow.FlowFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.sequence.reactors.MutableReactors;
import com.purplepip.odin.sequence.reactors.TriggerReactor;
import com.purplepip.odin.sequence.triggers.Action;
import org.junit.Before;
import org.junit.Test;

public class ReactorReceiverTest {
  private BeatClock clock;
  private FlowFactory<Note> flowFactory;
  private MeasureProvider measureProvider;

  /**
   * Set up test.
   */
  @Before
  public void setUp() {
    clock = new BeatClock(new StaticBeatsPerMinute(120));
    flowFactory = new FlowFactory<>(new DefaultFlowConfiguration());
    measureProvider = new StaticBeatMeasureProvider(4);
  }

  @Test
  public void send() throws Exception {
    MutableReactors reactors = new MutableReactors();
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withName("trigger1").withNote(60).addNoteTrigger()
      .addLayer("layer1")
      .withName("track1").withLayers("layer1").withEnabled(false)
      .withTrigger("trigger1", Action.ENABLE).addPattern(BEAT, 7);
    Things<Conductor> conductors = new MutableConductors();
    MutableTracks tracks = new MutableTracks();
    tracks.refresh(() -> project.getSequences().stream(),
        this::createSequenceTrack, conductors);
    reactors.refresh(() -> project.getTriggers().stream(), TriggerReactor::new,
        conductors, tracks);
    MetricRegistry metricRegistry = new MetricRegistry();
    ReactorReceiver receiver = new ReactorReceiver(reactors, metricRegistry);
    assertFalse(((SequenceTrack) tracks.findByName("track1")).getSequenceRoll().isEnabled());
    receiver.send(new NoteOnOperation(0,60,50), -1);
    assertEquals(1, metricRegistry
        .meter("receiver.triggered").getCount());
    assertTrue(((SequenceTrack) tracks.findByName("track1")).getSequenceRoll().isEnabled());
  }

  private SequenceTrack createSequenceTrack() {
    return new SequenceTrack(clock,
        new MutableSequenceRoll<>(clock, flowFactory , measureProvider));
  }
}