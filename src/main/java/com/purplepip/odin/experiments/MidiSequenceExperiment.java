package com.purplepip.odin.experiments;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midi.*;
import com.purplepip.odin.music.*;
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
        MidiDeviceWrapper midiDeviceWrapper = null;
        try {
            midiDeviceWrapper = new MidiDeviceWrapper();
            MeasureProvider measureProvider = new StaticMeasureProvider(4);
            sequencer = new OdinSequencer(
                    new OdinSequencerConfiguration()
                            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
                            .setMeasureProvider(measureProvider)
                            .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
                            .setMicrosecondPositionProvider(new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

            new SequenceBuilder(sequencer, measureProvider)
                    .addMetronome()
                    .addPattern(Tick.BEAT, 2)
                    .withChannel(9) .withNote(42)   .addPattern(Tick.QUARTER, 61435)
                                                    .addPattern(Tick.EIGHTH, 127)
                                    .withNote(46)   .addPattern(Tick.TWO_THIRDS, 7);

            new MidiSystemHelper().logInfo();
            sequencer.start();

            try {
                Thread.sleep(2000000);
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
