package com.purplepip.odin.midi.experiments;

import com.purplepip.odin.midi.*;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.series.StaticBeatsPerMinute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            MeasureProvider measureProvider = new StaticMeasureProvider(4);
            sequencer = new OdinSequencer(
                    new OdinSequencerConfiguration()
                            .setCoreJavaSequencerEnabled(false)
                            .setBeatsPerMinute(new StaticBeatsPerMinute(140))
                            .setMeasureProvider(measureProvider));

            sequencer.addSeries(new Metronome(measureProvider), 0, 9);
            sequencer.addSeries(new Metronome(measureProvider), 0, 0);
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
