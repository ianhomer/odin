package com.purplepip.odin.experiments;

import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.configuration.Environments;
import com.purplepip.odin.demo.DemoLoaderPerformance;
import com.purplepip.odin.demo.DemoPerformances;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.operation.OperationReceiver;
import com.purplepip.odin.performance.ClassPerformanceLoader;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import com.purplepip.odin.performance.PerformanceLoader;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Midi Sequence Experiment.
 */
@Slf4j
public class MidiSequenceExperiment {

  /**
   * Main method.
   *
   * @param args arguments
   */
  public static void main(String[] args) throws InterruptedException {
    System.out.println("Logging : " + LOG.getClass());
    MidiSequenceExperiment experiment = new MidiSequenceExperiment();
    experiment.doExperiment();
  }

  private void doExperiment() throws InterruptedException {
    final CountDownLatch lock = new CountDownLatch(1_000_000);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.trace("Received operation {}", operation);
      if (operation.hasCause()) {
        LOG.info("Caused Operation : {}", operation);
      } else if (operation instanceof LoadPerformanceOperation) {
        LOG.info("Load Performance Operation Operation : {}", operation);
      }
    };

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper();

    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    OperationTransmitter transmitter = new DefaultOperationTransmitter();
    midiDeviceWrapper.registerWithTransmitter(transmitter);
    DefaultPerformanceContainer container =
        new DefaultPerformanceContainer(new TransientPerformance());
    PerformanceLoader loader = new ClassPerformanceLoader(
        new DemoPerformances().getPerformances(), container, new DemoLoaderPerformance());
    OdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration()
        .setBeatsPerMinute(new StaticBeatsPerMinute(120))
        .setMeasureProvider(measureProvider)
        .setOperationReceiver(
            new OperationReceiverCollection(
                new MidiOperationReceiver(midiDeviceWrapper),
                loader,
                operationReceiver)
        )
        .setOperationTransmitter(transmitter)
        .setMicrosecondPositionProvider(midiDeviceWrapper.getReceivingDevice());
    try {
      sequencer = new OdinSequencer(configuration);
      SynthesizerHelper synthesizerHelper;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }

      container.addApplyListener(sequencer);
      loader.load(new ClassUri(GroovePerformance.class, true).getUri());
      container.apply();
      Environments.newEnvironment().dump();

      sequencer.prepare();
      sequencer.start();

      try {
        lock.await(1_000_000, TimeUnit.MILLISECONDS);
      } finally {
        sequencer.stop();
      }
    } finally {
      LOG.info("... stopping");
      if (sequencer != null) {
        sequencer.stop();
        sequencer.shutdown();
      }
      midiDeviceWrapper.close();
      LOG.debug("Metrics created : {}", configuration.getMetrics().getNames());
    }
  }
}
