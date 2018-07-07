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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiTransmitterBinding implements  AutoCloseable {
  private List<Transmitter> transmitters = new ArrayList<>();
  private List<Receiver> receivers = new ArrayList<>();

  /**
   * Bind MIDI device with operation transmitter.
   *
   * @param device device
   * @param operationTransmitter operation transmitter
   */
  public void bind(MidiDevice device, OperationTransmitter operationTransmitter) {
    if (!device.isSource()) {
      throw new OdinRuntimeException("Cannot bind transmitter to " + device
          + " since it is not a source");
    }
    try {
      Transmitter transmitter = device.getMidiDevice().getTransmitter();
      transmitters.add(transmitter);
      Receiver receiver = new MidiInputReceiver(operationTransmitter, device);
      receivers.add(receiver);
      transmitter.setReceiver(receiver);
      LOG.info("Registered receiver for {}", device);
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot register receiver : {}", e.getMessage());
      LOG.debug("Cannot register receiver", e);
    }
  }

  /**
   * Close binding.
   */
  public void close() {
    transmitters.forEach(Transmitter::close);
    receivers.forEach(Receiver::close);
    LOG.debug("Binding closed");
  }
}
