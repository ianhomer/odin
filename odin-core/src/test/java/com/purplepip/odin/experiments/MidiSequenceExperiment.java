package com.purplepip.odin.experiments;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.ProjectBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Midi SequenceRuntime Experiment.
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
    final CountDownLatch lock = new CountDownLatch(400);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.info("Received operation {}", operation);
    };

    LOG.info("Creating sequence");
    OdinSequencer sequencer = null;
    MidiDeviceWrapper midiDeviceWrapper = null;
    try {
      midiDeviceWrapper = new MidiDeviceWrapper();
      MeasureProvider measureProvider = new StaticMeasureProvider(4);
      sequencer = new OdinSequencer(
          new DefaultOdinSequencerConfiguration()
              .setBeatsPerMinute(new StaticBeatsPerMinute(120))
              .setMeasureProvider(measureProvider)
              .setOperationReceiver(
                  new OperationReceiverCollection(
                      new MidiOperationReceiver(midiDeviceWrapper),
                      operationReceiver)
              )
              .setMicrosecondPositionProvider(
                  new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

      SynthesizerHelper synthesizerHelper = null;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
        synthesizerHelper.logInstruments();
      }
      ProjectContainer container = new ProjectContainer(new TransientProject());
      new ProjectBuilder(container)
          .addMetronome()
          .withChannel(1).changeProgramTo("bird")
          .withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
          .withChannel(2).changeProgramTo("aahs")
          .withVelocity(20).withNote(42).addPattern(Ticks.BEAT, 15)
          .withChannel(9).changeProgramTo("TR-909")
          .withVelocity(100).withNote(62).addPattern(Ticks.BEAT, 2)
          .withVelocity(40).addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);
      container.addApplyListener(sequencer);
      container.apply();
      sequencer.start();

      try {
        lock.await(8000, TimeUnit.MILLISECONDS);
      } finally {
        sequencer.stop();
      }
      LOG.info("... stopping");
    } finally {
      if (sequencer != null) {
        sequencer.stop();
      }
      if (midiDeviceWrapper != null) {
        midiDeviceWrapper.close();
      }
    }
  }
}
