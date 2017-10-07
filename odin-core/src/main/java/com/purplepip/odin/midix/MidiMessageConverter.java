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
import com.purplepip.odin.midi.RawMessage;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequencer.Operation;
import java.util.Arrays;
import javax.sound.midi.MidiMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiMessageConverter {
  private MidiMessage midiMessage;

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
    if (midiMessage.getLength() == 3) {
      if (midiMessage.getMessage()[0] == (byte) RawMessage.NOTE_ON) {
        return new NoteOnOperation(0,
            midiMessage.getMessage()[1],
            midiMessage.getMessage()[2]);
      } else {
        throw new OdinException("MIDI message " + Arrays.toString(midiMessage.getMessage())
            + " with signal " + midiMessage.getMessage()[0]
            + " not currently mapped to an operation");
      }
    } else {
      throw new OdinException(
          "MIDI message " + Arrays.toString(midiMessage.getMessage())
              + " with length " + midiMessage.getLength()
              + " not currently mapped to an operation");
    }
  }
}
