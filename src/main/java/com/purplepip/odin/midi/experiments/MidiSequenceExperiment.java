package com.purplepip.odin.midi.experiments;

import com.purplepip.odin.midi.*;
import com.purplepip.odin.music.*;
import com.purplepip.odin.series.StaticBeatsPerMinute;
import com.purplepip.odin.series.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Midi Sequence Experiment.
 */
public class MidiSequenceExperiment {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSequenceExperiment.class);

    public static void main(String [] args)  {
        MidiSequenceExperiment experiment = new MidiSequenceExperiment();
        try {
            experiment.doExperiment();
        } catch (MidiException e) {
            LOG.error("Unexpected failure", e);
        }
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
            sequencer.addSeries(new Pattern(measureProvider, Tick.QUARTER, 12, new DefaultNote()),
                    0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.EIGHTH, 127, new DefaultNote(42)),
                    0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.FOUR_THIRDS, 7, new DefaultNote(46)),
                    0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.BEAT, 6, new DefaultNote(62, 20)),
                    0, 0);
            new MidiSystemHelper().logInfo().logInstruments();
            sequencer.start();

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
