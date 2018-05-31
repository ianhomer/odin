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
import com.purplepip.odin.operation.InvalidChannelOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationHandler;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * MIDI operation receiver.
 */
@Slf4j
public class MidiOperationReceiver implements OperationHandler {
  private MidiDeviceReceiver midiDeviceReceiver;

  public MidiOperationReceiver(MidiDeviceReceiver midiDeviceReceiver) {
    this.midiDeviceReceiver = midiDeviceReceiver;
  }

  @Override
  public void handle(Operation operation, long time) throws OdinException {
    if (!(operation instanceof ChannelOperation)) {
      LOG.trace("Ignoring non channel based operation");
      return;
    }

    ChannelOperation resolvedOperation;
    if (operation instanceof ProgramChangeOperation) {
      ProgramChangeOperation programChangeOperation = (ProgramChangeOperation) operation;
      if (programChangeOperation.isAbsolute()) {
        resolvedOperation = programChangeOperation;
      } else {
        resolvedOperation = resolveNonAbsolute(programChangeOperation);
        if (resolvedOperation instanceof InvalidChannelOperation) {
          LOG.warn(((InvalidChannelOperation) resolvedOperation).getMessage());
          return;
        }
      }
      LOG.debug("Program change operation : {}", resolvedOperation);
    } else {
      resolvedOperation = (ChannelOperation) operation;
    }

    if (midiDeviceReceiver.send(createMidiMessage(resolvedOperation), time)) {
      LOG.debug("Sent MIDI {} for time {}", resolvedOperation, time);
    }
  }

  private ChannelOperation resolveNonAbsolute(ProgramChangeOperation programChangeOperation)
      throws OdinException {
    if (midiDeviceReceiver.isOpenSynthesizer()) {
      /*
       * Handle program change operation by resolving string name for program.
       */
      Instrument instrument = midiDeviceReceiver
          .findInstrument(programChangeOperation.getChannel(),
              programChangeOperation.getProgramName());
      return new ProgramChangeOperation(programChangeOperation.getChannel(),
          instrument.getPatch().getBank(), instrument.getPatch().getProgram(),
          programChangeOperation.getProgramName());
    } else {
      return new InvalidChannelOperation(programChangeOperation,
          "Cannot change instrument since no open synthesizer available");
    }
  }

  private static MidiMessage
      createMidiMessage(ChannelOperation operation) throws OdinException {
    return new RawMidiMessage(new RawMessage(operation).getBytes());
  }

  @Override
  public void onPerformanceStart() {
    midiDeviceReceiver.onPerformanceStart();
  }

  @Override
  public void onPerformanceStop() {
    midiDeviceReceiver.onPerformanceStop();
  }
}
