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

package com.purplepip.odin.creation.track;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.purplepip.odin.bag.AbstractUnmodifiableThings;
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.conductor.Conductor;
import com.purplepip.odin.creation.conductor.LayerConductor;
import com.purplepip.odin.creation.conductor.LayerConductors;
import com.purplepip.odin.creation.conductor.UnmodifiableConductors;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.Test;

public class MutableTracksTest {
  private LayerConductors conductors = new LayerConductors();
  private AbstractUnmodifiableThings<Conductor> immutableConductors =
      new UnmodifiableConductors(conductors);

  private PerformanceContainer container = new PerformanceContainer(new TransientPerformance());
  private BeatClock clock = newPrecisionBeatClock(12000);
  private FlowFactory<Note> flowFactory = newNoteFlowFactory();
  private MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
  private SequenceTracks tracks = new SequenceTracks(immutableConductors);

  @Test
  public void testRefresh() throws OdinException {
    clock.start();
    new PerformanceBuilder(container)
        .addLayer("layer").withLayers("layer")
        .withProperty("notation", "C")
        .withFlowName("notation").addSequence();
    refresh();
    SequenceRollTrack track = tracks.stream()
        .findFirst()
        .orElseThrow(OdinException::new);

    assertEquals("notation", track.getSequence().getType());
    assertEquals(GenericSequence.class.getName(), track.getSequence().getClass().getName());
    assertEquals(Notation.class.getName(),
        track.getSequenceRoll().getFlow().getSequence().getClass().getName());
    assertEquals(0, track.getChannel());
  }

  @Test
  public void testEmptyNotation() throws OdinException {
    clock.start();
    new PerformanceBuilder(container)
        .withProperty("notation", "")
        .withFlowName("notation").addSequence();
    refresh();
    assertFalse("Notation track should not have been added since it is empty", tracks.stream()
        .findFirst().isPresent());
  }

  private void refresh() {
    conductors.refresh(
        container.getLayerStream(),
        layer -> new LayerConductor(layer, clock));
    tracks.refresh(
        container.getSequenceStream(),
        sequence -> new SequenceRollTrack(sequence, clock, measureProvider, flowFactory));
  }
}