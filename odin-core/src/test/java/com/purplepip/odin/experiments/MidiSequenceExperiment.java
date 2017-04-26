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

import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

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
          .addPattern(Tick.BEAT, 2)
          .withChannel(1).withNote(62).addPattern(Tick.BEAT, 4)
          .withChannel(9).withVelocity(40).withNote(42).addPattern(Tick.QUARTER, 61435)
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
        midiDeviceWrapper.changeProgram(8,126);
        // TODO : How to change percussion on channel 9, neither of these work, perhaps
        // channel 10 (= 9+1) is locked to percussion.
        midiDeviceWrapper.changeProgram(9,1);

        if (synthesizerHelper != null) {
          // TODO : Using Synthesizer API seems to have no effect
          synthesizerHelper.changeProgram(1, 0, 14);
          synthesizerHelper.changeProgram(0, 128, 5);
          synthesizerHelper.changeProgram(9, 1);
        }
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
