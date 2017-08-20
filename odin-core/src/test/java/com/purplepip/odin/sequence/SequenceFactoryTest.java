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
import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

public class SequenceFactoryTest {
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
}