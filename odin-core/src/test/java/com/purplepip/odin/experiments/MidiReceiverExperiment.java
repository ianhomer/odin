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

package com.purplepip.odin.experiments;

import static com.purplepip.odin.system.Environments.newEnvironment;

import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.midix.MidiDevice;
import com.purplepip.odin.midix.MidiHandle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidiReceiverExperiment {
  public static void main(String[] args)
      throws InterruptedException, MidiUnavailableException, DeviceUnavailableException {
    MidiReceiverExperiment experiment = new MidiReceiverExperiment();
    experiment.doExperiment();
  }

  private void doExperiment()
      throws InterruptedException, MidiUnavailableException, DeviceUnavailableException {
    LOG.debug("running experiment");
    final CountDownLatch lock = new CountDownLatch(100);

    try (MidiDevice source = newEnvironment().findOneSourceOrThrow(MidiHandle.class)) {
      try (Transmitter transmitter = source.getTransmitter()) {
        transmitter.setReceiver(new LoggingInputReceiver(transmitter.toString()));
        try {
          lock.await(20000, TimeUnit.MILLISECONDS);
        } finally {
          LOG.debug("... stopping experiment");
        }
      }
    }
  }

  public class LoggingInputReceiver implements Receiver {
    private String name;

    LoggingInputReceiver(String name) {
      this.name = name;
    }

    public void send(MidiMessage message, long time) {
      LOG.debug("MIDI message received {} from {} at {}", message.getMessage(), name, time);
    }

    public void close() {
      LOG.debug("Closing {}", this);
    }
  }
}
