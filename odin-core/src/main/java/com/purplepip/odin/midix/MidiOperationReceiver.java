package com.purplepip.odin.midix;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.RawMessage;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.sequencer.ChannelOperation;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MIDI operation receiver.
 */
public class MidiOperationReceiver implements OperationReceiver {
  private static final Logger LOG = LoggerFactory.getLogger(MidiOperationReceiver.class);

  private MidiDeviceWrapper midiDeviceWrapper;

  public MidiOperationReceiver(MidiDeviceWrapper midiDeviceWrapper) {
    this.midiDeviceWrapper = midiDeviceWrapper;
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    if (operation instanceof ChannelOperation) {
      ChannelOperation resolvedOperation;
      if (operation instanceof ProgramChangeOperation) {
        ProgramChangeOperation programChangeOperation = (ProgramChangeOperation) operation;
        if (programChangeOperation.isAbsolute()) {
          resolvedOperation = programChangeOperation;
        } else {
          /*
           * Handle program change operation by resolving string name for program.
           */
          Instrument instrument = midiDeviceWrapper
              .findInstrument(programChangeOperation.getChannel(),
                  programChangeOperation.getProgramName());
          resolvedOperation = new ProgramChangeOperation(programChangeOperation.getChannel(),
              instrument.getPatch().getBank(), instrument.getPatch().getProgram());
        }
        LOG.debug("Program change operation : {}", resolvedOperation);
      } else {
        resolvedOperation = (ChannelOperation) operation;
      }
      MidiMessage midiMessage = createMidiMessage(resolvedOperation);
      try {
        midiDeviceWrapper.getDevice().getReceiver().send(midiMessage, time);
      } catch (MidiUnavailableException e) {
        throw new OdinException("Cannot send MIDI message for " + midiMessage, e);
      }
    } else {
      LOG.debug("Ignoring non channel based operation");
    }
  }

  private MidiMessage createMidiMessage(ChannelOperation operation) throws OdinException {
    return new RawMidiMessage(new RawMessage(operation).getBytes());
  }
}
