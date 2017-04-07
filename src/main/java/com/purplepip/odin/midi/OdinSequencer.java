package com.purplepip.odin.midi;

import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;

/**
 * Core Odin MIDI Sequencer.
 */
public class OdinSequencer {
    private static final Logger LOG = LoggerFactory.getLogger(OdinSequencer.class);

    private OdinSequencerConfiguration configuration;
    private MidiDevice device;
    private Sequencer sequencer;

    public OdinSequencer(OdinSequencerConfiguration configuration) throws MidiException {
        this.configuration = configuration;
        start();
    }

    private void init() throws MidiException, MidiUnavailableException {
        initDevice();
        if (configuration.isCoreJavaSequencerEnabled()) {
            initSequencer();
        }
    }

    private void initSequencer() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer(false);
        // TODO : Externalise sequencer configuration
        sequencer.setTempoInBPM(100);
        sequencer.setLoopCount(1);
        sequencer.getTransmitter().setReceiver(getReceiver());
        sequencer.open();
    }

    private Receiver getReceiver() throws MidiUnavailableException {
        if (device != null) {
            return device.getReceiver();
        }
        return MidiSystem.getReceiver();
    }

    private void initDevice() throws MidiException {
        // TODO : Externalise and prioritise external MIDI devices to connect to.
        MidiDevice device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
        LOG.debug("MIDI device : {}", device);
    }

    public void addSeries(Series<Note> series) throws MidiException {
        if (configuration.isCoreJavaSequencerEnabled()) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            try {
                Sequence sequence = new SequenceFactory().createSequence(series);
                sequencer.setSequence(sequence);
            } catch (InvalidMidiDataException e) {
                throw new MidiException("Cannot set sequence for sequencer", e);
            }
            sequencer.start();
            LOG.info("Sequence started");
        } else {
            // TODO : Build out an internal sequencer which is directly driven from multiple series
            throw new MidiRuntimeException("Odin default sequencer not yet created");
        }
    }


    public void start() throws MidiException {
        try {
            init();
        } catch (MidiUnavailableException e) {
            throw new MidiException(e);
        }
    }

    public void stop() {
        if (sequencer != null) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
        }
    }
}
