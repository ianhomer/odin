package com.purplepip.odin.experiments;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.*;
import com.purplepip.odin.music.*;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.series.StaticBeatsPerMinute;
import com.purplepip.odin.series.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;

/**
 * Midi Sequence Experiment.
 */
public class MidiSequenceExperiment {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSequenceExperiment.class);

    public static void main(String [] args)  {
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
        try {
            MidiDevice device = new MidiSystemHelper().getInitialisedDevice();
            MeasureProvider measureProvider = new StaticMeasureProvider(4);
            sequencer = new OdinSequencer(
                    new OdinSequencerConfiguration()
                            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
                            .setMeasureProvider(measureProvider)
                            .setOperationReceiver(new MidiOperationReceiver(device))
                            .setMicrosecondPositionProvider(new MidiDeviceMicrosecondPositionProvider(device)));

            sequencer.addSeries(new Metronome(measureProvider), 0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.BEAT, 2), 0, 0);
            sequencer.addSeries(new Pattern(measureProvider, Tick.QUARTER, 61435, new DefaultNote(42)),
                    0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.EIGHTH, 127, new DefaultNote(42)),
                    0, 9);
            sequencer.addSeries(new Pattern(measureProvider, Tick.TWO_THIRDS, 7, new DefaultNote(46)),
                    0, 9);
            //sequencer.addSeries(new Pattern(measureProvider, Tick.HALF, 6, new DefaultNote(62, 20)),
            //        0, 0);
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
