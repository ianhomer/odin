package com.purplepip.odin.midix;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.RawMessage;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;

/**
 * MIDI operation receiver.
 */
public class MidiOperationReceiver implements OperationReceiver {
  private MidiDeviceWrapper midiDeviceWrapper;

  public MidiOperationReceiver(MidiDeviceWrapper midiDeviceWrapper) {
    this.midiDeviceWrapper = midiDeviceWrapper;
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    MidiMessage midiMessage = createMidiMessage(operation);
    try {
      midiDeviceWrapper.getDevice().getReceiver().send(midiMessage, time);
    } catch (MidiUnavailableException e) {
      throw new OdinException("Cannot send MIDI message for " + midiMessage, e);
    }
  }

  private MidiMessage createMidiMessage(Operation operation) throws OdinException {
    return new RawMidiMessage(new RawMessage(operation).getBytes());
  }
}
