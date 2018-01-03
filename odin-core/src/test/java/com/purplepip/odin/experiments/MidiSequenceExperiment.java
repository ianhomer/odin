package com.purplepip.odin.experiments;

import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.KotlinPerformance;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiver;
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
    try {
      experiment.doExperiment();
    } catch (OdinException e) {
      LOG.error("Unexpected failure", e);
    }
  }

  private void doExperiment() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(1_000_000);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.trace("Received operation {}", operation);
    };

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper();

    MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
    OperationTransmitter transmitter = new DefaultOperationTransmitter();
    midiDeviceWrapper.registerWithTransmitter(transmitter);
    OdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration()
        .setBeatsPerMinute(new StaticBeatsPerMinute(120))
        .setMeasureProvider(measureProvider)
        .setOperationReceiver(
            new OperationReceiverCollection(
                new MidiOperationReceiver(midiDeviceWrapper),
                operationReceiver)
        )
        .setOperationTransmitter(transmitter)
        .setMicrosecondPositionProvider(
            new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper.getReceivingDevice()));
    try {
      sequencer = new OdinSequencer(configuration);
      SynthesizerHelper synthesizerHelper;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }
      PerformanceContainer container = new PerformanceContainer(
          new KotlinPerformance().getContext().getPerformance());

      container.addApplyListener(sequencer);
      container.apply();

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
