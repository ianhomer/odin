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

package com.purplepip.odin.properties.beany;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.sequence.DefaultNotation;
import com.purplepip.odin.music.sequence.DefaultPattern;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.GenericSequence;
import com.purplepip.odin.sequence.Sequence;
import org.junit.Test;

public class SetterTest {
  @Test
  public void testSetPropertyOnGeneric() {
    Sequence sequence = new GenericSequence();
    Setter setter = new Setter(sequence);
    setter.set("format", "test-format");
    assertEquals("test-format", sequence.getProperty("format"));
  }

  @Test
  public void testSetPropertyOnSpecific() {
    Notation sequence = new DefaultNotation();
    Setter setter = new Setter(sequence);
    setter.set("format", "test-format");
    assertEquals("test-format", sequence.getFormat());
  }

  @Test
  public void testSetPropertyOnGenericWithDeclared() {
    Sequence sequence = new GenericSequence();
    Setter setter = new Setter(sequence, Setter.Mode.DECLARED);
    try (LogCaptor captor = new LogCapture().from(Setter.class).warn().start()) {
      setter.set("format", "test-format");
      assertEquals(1, captor.size());
    }
    assertNull(sequence.getProperty("format"));
  }

  @Test
  public void testSetPropertyOnSpecificWithDeclared() {
    Notation sequence = new DefaultNotation();
    Setter setter = new Setter(sequence, Setter.Mode.DECLARED);
    setter.set("format", "test-format");
    assertEquals("test-format", sequence.getFormat());
  }

  @Test(expected = OdinRuntimeException.class)
  public void testNotMutable() {
    Sequence sequence = mock(Sequence.class);
    new Setter(sequence);
  }

  @Test
  public void testSetIntProperty() {
    Pattern sequence = new DefaultPattern();
    Setter setter = new Setter(sequence);
    setter.set("bits", 1);
    assertEquals(1, sequence.getBits());
  }

  @Test
  public void testSetNoteProperty() {
    Pattern sequence = new DefaultPattern();
    Setter setter = new Setter(sequence);
    setter.set("note", new DefaultNote(11,22,33));
    assertEquals(11, sequence.getNote().getNumber());
    assertEquals(22, sequence.getNote().getVelocity());
    assertEquals(Whole.valueOf(33), sequence.getNote().getDuration());
  }

  @Test
  public void testSetNotePropertyOnGeneric() {
    Sequence sequence = new GenericSequence();
    Setter setter = new Setter(sequence);
    setter.set("note", new DefaultNote(11,22,33));
    assertEquals("11", sequence.getProperty("note.number"));
    assertEquals("22", sequence.getProperty("note.velocity"));
    assertEquals("33", sequence.getProperty("note.duration"));
  }

}