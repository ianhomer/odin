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

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.DebugMessage;
import com.purplepip.odin.midi.Status;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.ControlChangeOperation;
import com.purplepip.odin.operation.Operation;
import javax.sound.midi.MidiMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiMessageConverter {
  private final MidiMessage midiMessage;

  public MidiMessageConverter(MidiMessage midiMessage) {
    this.midiMessage = midiMessage;
  }

  /**
   * Convert MIDI message to an operation.
   *
   * @return operation
   * @throws OdinException exception
   */
  public Operation toOperation() throws OdinException {
    // TODO : Implement correct mapping from MIDI message to operation.
    // TODO : Allow mapping of channel to something other than 1
    MidiMessageOperation midiMessageOperation = new MidiMessageOperation(midiMessage);
    if (midiMessage.getLength() == 3) {
      byte[] message = midiMessage.getMessage();
      Status status = Status.getMessage(message[0]);
      if (status == null) {
        throw new OdinException(new DebugMessage(midiMessage.getMessage())
            + " has unrecognised status");
      } else {
        switch (status) {
          case NOTE_ON:
            return new NoteOnOperation(1, message[1], message[2], midiMessageOperation);
          case NOTE_OFF:
            return new NoteOffOperation(1, message[1]);
          case CONTROL_CHANGE:
            return new ControlChangeOperation(1, message[1], message[2]);
          default:
            throw new OdinException(new DebugMessage(midiMessage.getMessage())
                + " not currently mapped to an operation");
        }
      }
    } else {
      throw new OdinException(
          new DebugMessage(midiMessage.getMessage())
              + " not currently mapped to an operation since length not expected");
    }
  }
}
