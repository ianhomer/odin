package com.purplepip.odin.experiments;

import static com.purplepip.odin.music.notes.Notes.newNote;

import com.codahale.metrics.ConsoleReporter;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.sequence.Action;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.BeanyPerformanceBuilder;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.DefaultOperationTransmitter;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.OperationTransmitter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Midi Sequence Experiment.
 */
public class MidiSequencePlayground {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSequencePlayground.class);

  /**
   * Main method.
   *
   * @param args arguments
   */
  public static void main(String[] args) throws InterruptedException {
    MidiSequencePlayground experiment = new MidiSequencePlayground();
    try {
      experiment.doExperiment();
    } catch (OdinException e) {
      LOG.error("Unexpected failure", e);
    }
  }

  private void doExperiment() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(800);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.info("Received operation {}", operation);
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
      PerformanceContainer container = new PerformanceContainer(new TransientPerformance());
      new BeanyPerformanceBuilder(container)
          .addLayer("groove")
          .withChannel(1).changeProgramTo("piano")
          .withLayers("groove")
          .withEnabled(false)
          .withTrigger("trigger", Action.ENABLE)
          .withName("success").addNotation(Ticks.BEAT, "B4/8, C, D, E");

      container.addTrigger(
          new PatternNoteTrigger()
              .patternName("random")
              .name("trigger"));

      GenericSequence sequence = new Random()
          .lower(60).upper(72)
          .bits(1).note(newNote())
          .channel(1);

      sequence.setName("random");
      sequence.setTick(Ticks.BEAT);
      sequence.setEnabled(true);
      sequence.addLayer("groove");
      sequence.afterPropertiesSet();
      container.addSequence(sequence);

      container.addApplyListener(sequencer);
      container.apply();

      try (ConsoleReporter reporter = ConsoleReporter.forRegistry(configuration.getMetrics())
          .convertRatesTo(TimeUnit.SECONDS)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .build()) {
        // Report metrics
        reporter.start(1, TimeUnit.SECONDS);
        sequencer.start();

        try {
          lock.await(8000, TimeUnit.MILLISECONDS);
        } finally {
          sequencer.stop();
        }
      }
      LOG.info("... stopping");
    } finally {
      if (sequencer != null) {
        sequencer.stop();
      }
      midiDeviceWrapper.close();
      LOG.debug("Metrics created : {}", configuration.getMetrics().getNames());
    }
  }
}
