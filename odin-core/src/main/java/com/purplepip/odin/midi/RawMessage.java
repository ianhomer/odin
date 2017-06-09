/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.midi;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.AbstractNoteVelocityOperation;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.sequencer.ChannelOperation;

/**
 * Midi NoteOnOperation.  Portable implementation that can be used on both PC and Android.
 */
public class RawMessage {
  /*
   * See https://www.midi.org/specifications/item/table-1-summary-of-midi-message
   */
  private static final int NOTE_OFF = 0x80;
  private static final int NOTE_ON = 0x90;
  private static final int PROGRAM_CHANGE = 0xC0;

  private byte[] buffer = new byte[3];
  private int length;

  /**
   * Create a raw MIDI message from the given sequence operation.
   *
   * @param operation operation from which to create MIDI message
   * @throws OdinException Exception
   */
  public RawMessage(ChannelOperation operation) throws OdinException {
    if (operation instanceof NoteOnOperation) {
      handle(NOTE_ON, (AbstractNoteVelocityOperation) operation);
    } else if (operation instanceof NoteOffOperation) {
      handle(NOTE_OFF, (AbstractNoteVelocityOperation) operation);
    } else if (operation instanceof ProgramChangeOperation) {
      handle((ProgramChangeOperation) operation);
    } else {
      throw new OdinException("Operation " + operation.getClass() + " not recognised");
    }
  }

  private void handle(int status, AbstractNoteVelocityOperation operation) {
    setStatus(status, operation.getChannel());
    buffer[1] = (byte) (operation.getNumber() & 0xFF);
    buffer[2] = (byte) (operation.getVelocity() & 0xFF);
    length = 3;
  }

  private void handle(ProgramChangeOperation operation) {
    setStatus(PROGRAM_CHANGE, operation.getChannel());
    buffer[1] = (byte) (operation.getProgram() & 0xFF);
    buffer[2] = (byte) ((operation.getBank() >> 7) & 0xFF);
    length = 3;
  }

  private void setStatus(int command, int channel) {
    buffer[0] = (byte) (((command & 0xF0) | (channel & 0x0F)) & 0xFF);
  }

  public byte[] getBytes() {
    return buffer;
  }

  public int getLength() {
    return length;
  }
}
