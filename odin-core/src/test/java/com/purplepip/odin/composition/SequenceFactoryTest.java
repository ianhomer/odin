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

package com.purplepip.odin.composition;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.composition.clock.Clock;
import com.purplepip.odin.composition.flow.DefaultFlowConfiguration;
import com.purplepip.odin.composition.flow.Flow;
import com.purplepip.odin.composition.measure.MeasureProvider;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SequenceFactoryTest {
  private SequenceFactory<Note> factory = SequenceFactory
      .newNoteSequenceFactory(new DefaultFlowConfiguration());

  @Mock
  private Clock clock;

  @Mock
  private MeasureProvider measureProvider;

  @Test
  public void testCopy() throws OdinException {
    ProjectContainer container = new ProjectContainer(new TransientProject());
    new ProjectBuilder(container)
        .addLayer("groove")
        .withLayers("groove")
        .withOffset(4)
        .withLength(16)
        .addMetronome();
    SequenceConfiguration sequence = container.getSequenceStream()
        .findFirst().orElseThrow(OdinException::new);
    Metronome metronome = factory.newInstance(sequence, Metronome.class);
    assertEquals(16, metronome.getLength());
    assertEquals(1, metronome.getLayers().size());
    assertEquals("groove", metronome.getLayers().get(0));
  }

  @Test
  public void testGetSequenceClass() {
    assertEquals(Notation.class, factory.getClass("notation"));
    assertEquals(Metronome.class, factory.getClass("metronome"));
    assertEquals(Pattern.class, factory.getClass("pattern"));
  }

  @Test
  public void testCreateFlow() throws OdinException {
    SequenceFactory<Note> flowFactory =
        SequenceFactory.newNoteSequenceFactory(new DefaultFlowConfiguration());
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addMetronome();
    Flow<Sequence<Note>, Note> flow =
        flowFactory.createFlow(project.getSequences().iterator().next(), clock, measureProvider);
    assertEquals("DefaultFlow", flow.getClass().getSimpleName());
  }
}