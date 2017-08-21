/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.NotationFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.music.sequence.DefaultNotation;
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

public class SequenceFactoryTest {
  private SequenceFactory sequenceFactory = new SequenceFactory();

  @Test
  public void testCopy() throws OdinException {
    ProjectContainer container = new ProjectContainer(new TransientProject());
    new ProjectBuilder(container)
        .addLayer("groove")
        .withLayers("groove")
        .withOffset(4)
        .withLength(16)
        .addMetronome();
    Sequence sequence = container.getSequenceStream()
        .findFirst().orElseThrow(OdinException::new);
    SequenceFactory factory = new SequenceFactory();
    Metronome metronome = factory.createTypedCopy(DefaultMetronome.class, sequence);
    assertEquals(16, metronome.getLength());
    assertEquals(1, metronome.getLayers().size());
    assertEquals("groove", metronome.getLayers().get(0));
  }

  @Test
  public void testGetFlowClass() {
    assertEquals(NotationFlow.class, sequenceFactory.getFlowClass("Notation"));
    assertEquals(MetronomeFlow.class, sequenceFactory.getFlowClass("Metronome"));
    assertEquals(PatternFlow.class, sequenceFactory.getFlowClass("Pattern"));
  }

  @Test
  public void testGetSequenceClass() {
    assertEquals(Notation.class, sequenceFactory.getSequenceClass("Notation"));
    assertEquals(Metronome.class, sequenceFactory.getSequenceClass("Metronome"));
    assertEquals(Pattern.class, sequenceFactory.getSequenceClass("Pattern"));
  }

  @Test
  public void testGetDefaultSequenceClass() {
    assertEquals(DefaultNotation.class,
        sequenceFactory.getDefaultSequenceClass("Notation"));
    assertEquals(DefaultMetronome.class,
        sequenceFactory.getDefaultSequenceClass("Metronome"));
    assertEquals(DefaultPattern.class,
        sequenceFactory.getDefaultSequenceClass("Pattern"));
  }
}