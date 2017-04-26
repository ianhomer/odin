package com.purplepip.odin.experiments;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiSystemHelper;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.SequenceBuilder;
import com.purplepip.odin.series.StaticBeatsPerMinute;
import com.purplepip.odin.series.Tick;

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
          new OdinSequencerConfiguration()
              .setBeatsPerMinute(new StaticBeatsPerMinute(120))
              .setMeasureProvider(measureProvider)
              .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
              .setMicrosecondPositionProvider(
                  new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

      new SequenceBuilder(sequencer, measureProvider)
          .addMetronome()
          .withChannel(1).withVelocity(10).withNote(62).addPattern(Tick.BEAT, 4)
          .withChannel(2).withVelocity(70).withNote(62).addPattern(Tick.BEAT, 2)
          .withChannel(8).withVelocity(100).withNote(42).addPattern(Tick.BEAT, 15)
          .withChannel(2).withVelocity(70).withNote(62).addPattern(Tick.BEAT, 2)
          .withVelocity(20)
          .addPattern(Tick.EIGHTH, 127)
          .withNote(46).addPattern(Tick.TWO_THIRDS, 7);

      SynthesizerHelper synthesizerHelper = null;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
        synthesizerHelper.logInstruments();
      }

      if (midiDeviceWrapper.isGervill()) {
        midiDeviceWrapper.changeProgram(0,41);
        midiDeviceWrapper.changeProgram(1,0, 123);
        midiDeviceWrapper.changeProgram(2,4096, 52);
        midiDeviceWrapper.changeProgram(8,26);
        // Note that channel 9, percussion has different program numbers to other channels
        midiDeviceWrapper.changeProgram(9,26);
      }

      new MidiSystemHelper().logInfo();


      sequencer.start();

      try {
        Thread.sleep(4000);
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
