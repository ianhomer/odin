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

package com.purplepip.odin.music.sequence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.composition.sequence.SequenceConfiguration;
import com.purplepip.odin.music.notes.DefaultNote;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MetronomeTest {
  @Test
  public void testCopy() {
    Metronome metronome = new Metronome();
    metronome.setNoteBarMid(new DefaultNote(1,2,3));
    SequenceConfiguration copy = metronome.copy();
    assertTrue(copy != null);
    Metronome metronomeCopy = (Metronome) copy;
    assertEquals(1, metronomeCopy.getNoteBarMid().getNumber());
  }

  @Test
  public void testEquals() {
    Metronome metronome1 = new Metronome();
    Metronome metronome2 = new Metronome();
    LOG.debug("Created {} and {}", metronome1, metronome2);
    assertFalse(metronome1.equals(metronome2));
  }
}