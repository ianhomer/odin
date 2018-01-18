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

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.measure.StaticBeatMeasureProvider.newMeasureProvider;
import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static com.purplepip.odin.configuration.TriggerFactories.newTriggerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.action.DisableAction;
import com.purplepip.odin.creation.action.EnableAction;
import com.purplepip.odin.creation.conductor.LayerConductor;
import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.reactors.TriggerReactor;
import com.purplepip.odin.creation.reactors.TriggerReactors;
import com.purplepip.odin.creation.track.SequenceRollTrack;
import com.purplepip.odin.creation.track.SequenceTracks;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import org.junit.Test;

public class ReactorReceiverTest {
  private TriggerFactory triggerFactory = newTriggerFactory();
  private FlowFactory flowFactory = newNoteFlowFactory();
  private ActionFactory actionFactory = newActionFactory();
  private BeatClock clock = newPrecisionBeatClock(120);
  private MeasureProvider measureProvider = newMeasureProvider(4);

  @Test
  public void testSend() throws Exception {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder
        .withName("trigger1").withNote(60).addNoteTrigger()
        .withName("trigger2").withNote(61).addNoteTrigger()
        .addLayer("layer1")
        .withName("track1").withLayers("layer1").withEnabled(false)
        .withTrigger("trigger1", new EnableAction())
        .withTrigger("trigger2", new DisableAction())
        .addPattern(BEAT, 7);
    LayerConductors conductors = new LayerConductors();
    conductors.refresh(
        project.getLayers().stream(),
        layer -> new LayerConductor(layer, clock));
    SequenceTracks tracks = new SequenceTracks(conductors);
    tracks.refresh(
        project.getSequences().stream(), sequence ->
            new SequenceRollTrack(sequence, clock, measureProvider, flowFactory, actionFactory));
    TriggerReactors reactors = new TriggerReactors(tracks, conductors);
    reactors.refresh(
        project.getTriggers().stream(),
        trigger -> new TriggerReactor(trigger, triggerFactory));
    MetricRegistry metricRegistry = new MetricRegistry();
    ReactorReceiver receiver = new ReactorReceiver(reactors, metricRegistry,
        new LoggingOperationReceiver());
    assertFalse(tracks.findByName("track1").getSequenceRoll().isEnabled());

    /*
     * Send note on operation to trigger enabling track
     */
    receiver.handle(new NoteOnOperation(0,60,50), -1);
    assertEquals(1, metricRegistry.meter("receiver.triggered").getCount());
    assertTrue(tracks.findByName("track1").isEnabled());

    /*
     * Send note on operation to trigger disabling track
     */
    receiver.handle(new NoteOnOperation(0,61, 50), -1);
    assertEquals(2, metricRegistry.meter("receiver.triggered").getCount());
    assertFalse(tracks.findByName("track1").isEnabled());
  }
}