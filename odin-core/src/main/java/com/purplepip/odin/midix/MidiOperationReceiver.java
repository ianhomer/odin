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
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.operation.ChannelOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import lombok.extern.slf4j.Slf4j;

/**
 * MIDI operation receiver.
 */
@Slf4j
public class MidiOperationReceiver implements OperationReceiver {
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
          if (midiDeviceWrapper.isOpenSynthesizer()) {
            /*
             * Handle program change operation by resolving string name for program.
             */
            Instrument instrument = midiDeviceWrapper
                .findInstrument(programChangeOperation.getChannel(),
                    programChangeOperation.getProgramName());
            resolvedOperation = new ProgramChangeOperation(programChangeOperation.getChannel(),
                instrument.getPatch().getBank(), instrument.getPatch().getProgram(),
                programChangeOperation.getProgramName());
          } else {
            LOG.warn("Cannot change instrument since no open synthesizer available");
            return;
          }
        }
        LOG.debug("Program change operation : {}", resolvedOperation);
      } else {
        resolvedOperation = (ChannelOperation) operation;
      }
      MidiMessage midiMessage = createMidiMessage(resolvedOperation);
      try {
        MidiDevice receiver = midiDeviceWrapper.getReceivingDevice();
        if (receiver.isOpen()) {
          midiDeviceWrapper.getReceivingDevice().getReceiver().send(midiMessage, time);
        }
      } catch (MidiUnavailableException e) {
        throw new OdinException("Cannot send MIDI message for " + midiMessage, e);
      }
    } else {
      LOG.debug("Ignoring non channel based operation");
    }
  }

  private static MidiMessage
      createMidiMessage(ChannelOperation operation) throws OdinException {
    return new RawMidiMessage(new RawMessage(operation).getBytes());
  }
}
