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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.purplepip.odin.bag.AbstractUnmodifiableThings;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.SequenceFactory;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.conductor.Conductor;
import com.purplepip.odin.sequence.conductor.LayerConductor;
import com.purplepip.odin.sequence.conductor.MutableConductors;
import com.purplepip.odin.sequence.conductor.UnmodifiableConductors;
import com.purplepip.odin.sequence.flow.DefaultFlowConfiguration;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.sequence.roll.SequenceRollTrack;
import com.purplepip.odin.sequence.track.MutableTracks;
import org.junit.Test;

public class MutableTracksTest {
  private MutableConductors conductors = new MutableConductors();
  private AbstractUnmodifiableThings<Conductor> immutableConductors =
      new UnmodifiableConductors(conductors);

  private ProjectContainer container = new ProjectContainer(new TransientProject());
  private BeatClock clock = new BeatClock(new StaticBeatsPerMinute(12000));
  private SequenceFactory<Note> sequenceFactory =
      SequenceFactory.createNoteSequenceFactory(new DefaultFlowConfiguration());
  private MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
  private MutableTracks tracks = new MutableTracks();

  @Test
  public void testRefresh() throws OdinException {
    clock.start();
    new ProjectBuilder(container)
        .addLayer("layer").withLayers("layer")
        .withProperty("notation", "C")
        .withFlowName("notation").addSequence();
    refresh();
    SequenceRollTrack track = tracks.stream()
        .filter(t -> t instanceof SequenceRollTrack).map(t -> (SequenceRollTrack) t)
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
    new ProjectBuilder(container)
        .withProperty("notation", "")
        .withFlowName("notation").addSequence();
    refresh();
    assertFalse("Notation track should not have been added since it is empty", tracks.stream()
        .filter(t -> t instanceof SequenceRollTrack).map(t -> (SequenceRollTrack) t)
        .findFirst().isPresent());
  }

  private void refresh() {
    conductors.refresh(
        () -> container.getLayerStream(),
        () -> new LayerConductor(clock));
    tracks.refresh(
        () -> container.getSequenceStream(),
        () -> new SequenceRollTrack(clock, measureProvider, sequenceFactory),
        immutableConductors);
  }
}