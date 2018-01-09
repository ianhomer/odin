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

import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiInputReceiver implements Receiver {
  private OperationReceiver receiver;
  private MicrosecondPositionProvider microsecondPositionProvider;

  public MidiInputReceiver(OperationReceiver receiver,
                           MicrosecondPositionProvider microsecondPositionProvider) {
    this.receiver = receiver;
    this.microsecondPositionProvider = microsecondPositionProvider;
  }

  /**
   * Send MIDI message to receiver.
   *
   * @param message MIDI message sent
   * @param time time of message
   */
  @Override
  public void send(MidiMessage message, long time) {
    try {
      Operation operation = new MidiMessageConverter(message).toOperation();
      long deviceMicrosecondPosition = microsecondPositionProvider.getMicroseconds();
      LOG.debug("Received {} for time {} at time {} (diff = {})", operation, time,
          deviceMicrosecondPosition, time - deviceMicrosecondPosition);
      receiver.handle(operation, time);
    } catch (OdinException e) {
      LOG.error("Cannot relay MIDI message " + message, e);
    }
  }

  @Override
  public void close() {
    LOG.debug("Closing {}", this);
  }
}
