package com.purplepip.odin.midi.experiments;

import com.purplepip.odin.midi.*;
import com.purplepip.odin.music.Metronome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;

/**
 * Midi Sequence Experiment.
 */
public class MidiSequenceExperiment {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSequenceExperiment.class);

    public static void main(String [] args) throws MidiException {
        MidiSequenceExperiment experiment = new MidiSequenceExperiment();
        experiment.doExperiment();
    }

    private void doExperiment() throws MidiException {
        LOG.info("Creating sequence");
        OdinSequencer sequencer = null;
        try {
            sequencer = new OdinSequencer(
                    new OdinSequencerConfiguration()
                            .setCoreJavaSequencerEnabled(false)
                            .setBeatsPerMinute(140));
            sequencer.addSeries(new Metronome(4), 0, 9);
            sequencer.addSeries(new Metronome(4), 0, 0);
            new MidiSystemHelper().logInfo().logInstruments();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.error("Sleep interrupted", e);
            }
            LOG.info("... stopping");
        } finally {
            if (sequencer != null) {
                sequencer.stop();
            }
        }
    }

}
