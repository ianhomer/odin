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

import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.midix.MidiDevice;
import com.purplepip.odin.midix.MidiHandle;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiTransmitterBinding;
import com.purplepip.odin.midix.SynthesizerDevice;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.BeanyPerformanceBuilder;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.OperationTransmitter;
import com.purplepip.odin.system.Environments;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InputExperiment {
  public static void main(String[] args) throws InterruptedException, DeviceUnavailableException {
    InputExperiment experiment = new InputExperiment();
    experiment.doExperiment();
  }

  private void doExperiment() throws InterruptedException, DeviceUnavailableException {
    final CountDownLatch lock = new CountDownLatch(800);

    Environments.newEnvironment().dump();

    OperationHandler operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.info("Received operation {}", operation);
    };

    OdinSequencer sequencer = null;
    Environment environment = Environments.newEnvironment();
    MidiDevice device = environment.findOneSink(MidiHandle.class)
        .orElseThrow(DeviceUnavailableException::new);
    MidiTransmitterBinding binding = new MidiTransmitterBinding();

    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    OperationTransmitter transmitter = new DefaultOperationTransmitter();
    environment.findOneSource(MidiHandle.class)
        .ifPresent(it -> binding.bind(it, transmitter));
    OdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration()
        .setBeatsPerMinute(new StaticBeatsPerMinute(120))
        .setMeasureProvider(measureProvider)
        .setOperationReceiver(
            new OperationReceiverCollection(
                new MidiOperationReceiver(device),
                operationReceiver)
        )
        .setOperationTransmitter(transmitter)
        .setMicrosecondPositionProvider(device);
    try {
      sequencer = new OdinSequencer(configuration);
      if (device instanceof SynthesizerDevice) {
        ((SynthesizerDevice) device).loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }
      DefaultPerformanceContainer container =
          new DefaultPerformanceContainer(new TransientPerformance());
      new BeanyPerformanceBuilder(container)
          .withChannel(1).changeProgramTo("piano");

      container.addApplyListener(sequencer);
      container.apply();

      sequencer.start();

      try {
        lock.await(60, TimeUnit.SECONDS);
      } finally {
        sequencer.stop();
      }
      LOG.info("... stopping");
    } finally {
      if (sequencer != null) {
        sequencer.stop();
      }
      binding.close();
      device.close();
      LOG.debug("Metrics created : {}", configuration.getMetrics().getNames());
    }
  }
}
