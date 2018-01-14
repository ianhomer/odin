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

package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.Status;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.ControlChangeOperation;
import com.purplepip.odin.operation.Operation;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.junit.Test;

public class MidiMessageConverterTest {
  @Test
  public void testToOperation() throws Exception {
    MidiMessageConverter converter =
        new MidiMessageConverter(new ShortMessage(Status.NOTE_ON.getValue(),60,50));
    Operation operation = converter.toOperation();
    assertTrue(operation instanceof NoteOnOperation);
    NoteOnOperation noteOnOperation = (NoteOnOperation) operation;
    assertEquals(60, noteOnOperation.getNumber());
    assertEquals(50, noteOnOperation.getVelocity());
  }

  @Test
  public void testToNoteOffOperation() throws Exception {
    MidiMessageConverter converter =
        new MidiMessageConverter(new ShortMessage(Status.NOTE_OFF.getValue(),60,50));
    Operation operation = converter.toOperation();
    assertTrue(operation instanceof NoteOffOperation);
    NoteOffOperation noteOffOperation = (NoteOffOperation) operation;
    assertEquals(60, noteOffOperation.getNumber());
    // Note that velocity is not used for note off operation in this system.
    assertEquals(0, noteOffOperation.getVelocity());
  }

  @Test
  public void testControlChangeOperation() throws InvalidMidiDataException, OdinException {
    Operation operation = new MidiMessageConverter(new ShortMessage(-72,123,0))
        .toOperation();
    assertTrue(operation instanceof ControlChangeOperation);
    ControlChangeOperation controlChangeOperation = (ControlChangeOperation) operation;
    assertEquals(123, controlChangeOperation.getControl());
    assertEquals(0, controlChangeOperation.getValue());
  }
}