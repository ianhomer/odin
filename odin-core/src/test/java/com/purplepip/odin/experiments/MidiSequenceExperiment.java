package com.purplepip.odin.experiments;

import com.purplepip.odin.boot.GuaranteedMidiApplication;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.demo.DemoLoaderPerformance;
import com.purplepip.odin.demo.DemoPerformances;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.ClassPerformanceLoader;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import com.purplepip.odin.performance.PerformanceLoader;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.system.Environments;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/** Midi Sequence Experiment. */
@Slf4j
public class MidiSequenceExperiment {

  /**
   * Main method.
   *
   * @param args arguments
   * @throws InterruptedException interrupted
   * @throws DeviceUnavailableException device unavailable
   */
  public static void main(String[] args) throws InterruptedException, DeviceUnavailableException {
    System.out.println("Logging : " + LOG.getClass());
    MidiSequenceExperiment experiment = new MidiSequenceExperiment();
    experiment.doExperiment();
  }

  private void doExperiment() throws InterruptedException, DeviceUnavailableException {
    final CountDownLatch lock = new CountDownLatch(1_000_000);

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    DefaultOdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration();
    try (GuaranteedMidiApplication application = new GuaranteedMidiApplication()) {
      MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
      DefaultPerformanceContainer container =
          new DefaultPerformanceContainer(new TransientPerformance());
      PerformanceLoader loader =
          new ClassPerformanceLoader(
              new DemoPerformances().getPerformances(), container, new DemoLoaderPerformance());
      final OperationHandler operationReceiver =
          (operation, time) -> {
            lock.countDown();
            LOG.trace("Received operation {}", operation);
            if (operation.hasCause()) {
              LOG.info("Caused Operation : {}", operation);
            } else if (operation instanceof LoadPerformanceOperation) {
              LOG.info("Load Performance Operation Operation : {}", operation);
            }
          };

      configuration
          .setBeatsPerMinute(new StaticBeatsPerMinute(120))
          .setMeasureProvider(measureProvider)
          .setOperationReceiver(
              new OperationReceiverCollection(
                  new MidiOperationReceiver(application.getSink()), loader, operationReceiver))
          .setOperationTransmitter(application.getTransmitter())
          .setMicrosecondPositionProvider(application.getSink());
      sequencer = new OdinSequencer(configuration);
      application
          .getSynthesizer()
          .ifPresent(
              synthesizer ->
                  synthesizer.loadGervillSoundBank(
                      "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2"));

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
      LOG.debug("Metrics created : {}", configuration.getMetrics().getNames());
    }
  }
}
