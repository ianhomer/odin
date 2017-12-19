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

package com.purplepip.odin.creation.sequence;

import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static com.purplepip.odin.configuration.SequenceFactories.newNoteSequenceFactory;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.Clock;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.flow.DefaultFlowConfiguration;
import com.purplepip.odin.creation.flow.Flow;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SequenceFactoryTest {
  private SequenceFactory<Note> sequenceFactory = newNoteSequenceFactory();
  private FlowFactory<Note> flowFactory = newNoteFlowFactory(new DefaultFlowConfiguration());

  @Mock
  private Clock clock;

  @Mock
  private MeasureProvider measureProvider;

  @Test
  public void testCopy() throws OdinException {
    PerformanceContainer container = new PerformanceContainer(new TransientPerformance());
    new PerformanceBuilder(container)
        .addLayer("groove")
        .withLayers("groove")
        .withOffset(4)
        .withLength(16)
        .addMetronome();
    SequenceConfiguration sequence = container.getSequenceStream()
        .findFirst().orElseThrow(OdinException::new);
    Metronome metronome = sequenceFactory.newInstance(sequence, Metronome.class);
    assertEquals(Wholes.valueOf(16), metronome.getLength());
    assertEquals(1, metronome.getLayers().size());
    assertEquals("groove", metronome.getLayers().get(0));
  }

  @Test
  public void testGetSequenceClass() {
    assertEquals(Notation.class, sequenceFactory.getClass("notation"));
    assertEquals(Metronome.class, sequenceFactory.getClass("metronome"));
    assertEquals(Pattern.class, sequenceFactory.getClass("pattern"));
  }

  @Test
  public void testCreateFlow() throws OdinException {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder.addMetronome();
    Flow<Sequence<Note>, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next(), clock, measureProvider);
    assertEquals("DefaultFlow", flow.getClass().getSimpleName());
  }
}