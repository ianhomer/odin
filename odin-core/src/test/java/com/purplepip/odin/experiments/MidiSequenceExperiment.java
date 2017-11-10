package com.purplepip.odin.experiments;

import com.codahale.metrics.ConsoleReporter;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.measure.MeasureProvider;
import com.purplepip.odin.creation.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.creation.tick.Ticks;
import com.purplepip.odin.creation.triggers.Action;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequencer.BeanyProjectBuilder;
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
public class MidiSequenceExperiment {
  private static final Logger LOG = LoggerFactory.getLogger(MidiSequenceExperiment.class);

  /**
   * Main method.
   *
   * @param args arguments
   */
  public static void main(String[] args) throws InterruptedException {
    MidiSequenceExperiment experiment = new MidiSequenceExperiment();
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
            new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper));
    try {
      sequencer = new OdinSequencer(configuration);
      SynthesizerHelper synthesizerHelper;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }
      ProjectContainer container = new ProjectContainer(new TransientProject());
      new BeanyProjectBuilder(container)
          .withName("note-60-trigger").withNote(60).addNoteTrigger()
          .addLayer("groove")
          .withOffset(0).withLength(12).addLayer("start")
          .withOffset(8).withLength(8).addLayer("mid")
          .withLayers("groove")
          .withLength(-1).withOffset(0)
          .addMetronome()
          .withChannel(1).changeProgramTo("rock")
          .withTrigger("note-60-trigger", Action.ENABLE)
          .withEnabled(false).withVelocity(50).withNote(62).addPattern(Ticks.BEAT, 7)
          .withDefaults()
          .withLayers("start")
          .withChannel(2).changeProgramTo("aahs")
          .withVelocity(20).withNote(42).addPattern(Ticks.BEAT, 15)
          .withLayers("groove")
          .withChannel(9).changeProgramTo("TR-909")
          .withVelocity(100).withNote(62).addPattern(Ticks.BEAT, 2)
          .withVelocity(50).addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7)
          .withChannel(3).changeProgramTo("bass")
          .withLayers("mid")
          .withVelocity(100).addNotation(Ticks.BEAT, "B4/8, B4, E4/q, G4, C4");

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
