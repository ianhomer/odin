package com.purplepip.odin.experiments;

import static com.purplepip.odin.music.notes.Notes.newNote;

import com.codahale.metrics.ConsoleReporter;
import com.purplepip.odin.boot.SimpleMidiApplication;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.action.EnableAction;
import com.purplepip.odin.creation.sequence.SequencePlugin;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.BeanyPerformanceBuilder;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Midi Sequence Experiment. */
public class MidiSequencePlayground {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSequencePlayground.class);

  /**
   * Main method.
   *
   * @param args arguments
   */
  public static void main(String[] args) throws InterruptedException, DeviceUnavailableException {
    new MidiSequencePlayground().doExperiment();
  }

  private void doExperiment() throws InterruptedException, DeviceUnavailableException {
    final CountDownLatch lock = new CountDownLatch(800);

    OperationHandler operationReceiver =
        (operation, time) -> {
          lock.countDown();
          LOG.info("Received operation {}", operation);
        };

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    DefaultOdinSequencerConfiguration configuration = new DefaultOdinSequencerConfiguration();
    try (SimpleMidiApplication application = new SimpleMidiApplication()) {
      MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
      OperationTransmitter transmitter = new DefaultOperationTransmitter();
      configuration
          .setBeatsPerMinute(new StaticBeatsPerMinute(120))
          .setMeasureProvider(measureProvider)
          .setOperationReceiver(
              new OperationReceiverCollection(
                  new MidiOperationReceiver(application.getSink()), operationReceiver))
          .setOperationTransmitter(transmitter)
          .setMicrosecondPositionProvider(application.getSink());
      sequencer = new OdinSequencer(configuration);
      application
          .getSynthesizer()
          .ifPresent(
              synthesizer ->
                  synthesizer.loadGervillSoundBank(
                      "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2"));
      DefaultPerformanceContainer container =
          new DefaultPerformanceContainer(new TransientPerformance());
      new BeanyPerformanceBuilder(container)
          .addLayer("groove")
          .withChannel(1)
          .changeProgramTo("piano")
          .withLayers("groove")
          .withEnabled(false)
          .withTrigger("trigger", new EnableAction())
          .withName("success")
          .addNotation(Ticks.BEAT, "B4/8, C, D, E");

      container.addTrigger(new PatternNoteTrigger().patternName("random").name("trigger"));

      SequencePlugin sequence = new Random().lower(60).upper(72).bits(1).note(newNote()).channel(1);

      sequence.setName("random");
      sequence.setTick(Ticks.BEAT);
      sequence.setEnabled(true);
      sequence.addLayer("groove");
      sequence.initialise();
      container.addSequence(sequence);

      container.addApplyListener(sequencer);
      container.apply();

      try (ConsoleReporter reporter =
          ConsoleReporter.forRegistry(configuration.getMetrics())
              .convertRatesTo(TimeUnit.SECONDS)
              .convertDurationsTo(TimeUnit.MILLISECONDS)
              .build()) {
        // Report metrics
        reporter.start(1, TimeUnit.SECONDS);
        sequencer.prepare();
        sequencer.start();

        lock.await(8000, TimeUnit.MILLISECONDS);
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
