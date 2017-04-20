package com.purplepip.odin.midi;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationType;

/**
 * Midi Operation.  Portable implementation that can be used on both PC and Android.
 */
public class RawMessage {
  /*
   * See https://www.midi.org/specifications/item/table-1-summary-of-midi-message
   */
  public static final int NOTE_OFF = 0x80;
  public static final int NOTE_ON = 0x90;

  private byte[] buffer = new byte[3];
  private int length;

  /**
   * Create a raw MIDI message from the given sequence operation.
   *
   * @param operation operation from which to create MIDI message
   * @throws OdinException
   */
  public RawMessage(Operation operation) throws OdinException {
    // Very simple implementation for now, which just support note on and off.
    setStatus(getCommand(operation.getType()), operation.getChannel());
    buffer[1] = (byte) (operation.getNumber() & 0xFF);
    buffer[2] = (byte) (operation.getVelocity() & 0xFF);
    length = 3;
  }

  private int getCommand(OperationType type) throws OdinException {
    switch (type) {
      case ON:
        return NOTE_ON;
      case OFF:
        return NOTE_OFF;
      default:
        throw new OdinException("Operation " + type + " not recognised");
    }
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
