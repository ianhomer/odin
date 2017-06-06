package com.purplepip.odin.experiments;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiSystemHelper;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.SequenceBuilder;
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
  public static void main(String[] args) {
    MidiSequenceExperiment experiment = new MidiSequenceExperiment();
    try {
      experiment.doExperiment();
    } catch (OdinException e) {
      LOG.error("Unexpected failure", e);
    }
  }

  private void doExperiment() throws OdinException {

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
              .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
              .setMicrosecondPositionProvider(
                  new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

      new SequenceBuilder(sequencer.getProject())
          .addMetronome()
          .withChannel(1).withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
          .withChannel(2).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
          .withChannel(8).withVelocity(100).withNote(42).addPattern(Ticks.BEAT, 15)
          .withChannel(9).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
          .withVelocity(20)
          .addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

      SynthesizerHelper synthesizerHelper = null;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
        synthesizerHelper.logInstruments();
      }

      if (midiDeviceWrapper.isGervill()) {
        midiDeviceWrapper.changeProgram(1,"bird");
        midiDeviceWrapper.changeProgram(2,"aahs");
        midiDeviceWrapper.changeProgram(8,26);
        // Note that channel 9, percussion has different program numbers to other channels
        midiDeviceWrapper.changeProgram(9,26);
        midiDeviceWrapper.changeProgram(9, "TR-909");
      }

      new MidiSystemHelper().logInfo();


      sequencer.start();

      try {
        Thread.sleep(6000);
      } catch (InterruptedException e) {
        LOG.error("Sleep interrupted", e);
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
