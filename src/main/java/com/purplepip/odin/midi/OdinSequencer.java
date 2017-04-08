package com.purplepip.odin.midi;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Series;
import com.purplepip.odin.series.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.series.TimeUnit;
import com.purplepip.odin.series.TimeUnitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Core Odin MIDI Sequencer.
 */
public class OdinSequencer {
    private static final Logger LOG = LoggerFactory.getLogger(OdinSequencer.class);

    private OdinSequencerConfiguration configuration;
    private MidiDevice device;
    private Sequencer sequencer;
    private Set<Series<Note>> seriesSet = new HashSet<>();
    private SeriesProcessor seriesProcessor;
    private SeriesTimeUnitConverterFactory seriesTimeUnitConverterFactory;

    public OdinSequencer(OdinSequencerConfiguration configuration) throws MidiException {
        this.configuration = configuration;
        start();
    }

    private void init() throws MidiException, MidiUnavailableException {
        initDevice();
        if (configuration.isCoreJavaSequencerEnabled()) {
            initSequencer();
        }
        seriesProcessor = new SeriesProcessor(device, seriesSet);
        Thread thread = new Thread(seriesProcessor);
        thread.start();
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
        device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
        if (device == null) {
            device = new MidiSystemHelper().findMidiDeviceByName("Gervill");
        }
        LOG.debug("MIDI device : {}", device);
    }

    /**
     * Add series at the given time offset, where offset is in the time units of the series being added.
     *
     * @param series
     * @param offset
     * @throws MidiException
     */
    public void addSeries(Series<Note> series, long offset) throws MidiException {
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
            long deviceOffset = 0;
            switch (series.getTimeUnits()) {
                case BEAT:
                    // TODO : Address magic number
                    deviceOffset = offset * configuration.getBeatsPerMinute() * 60000 * 1000;
                    break;
                case MILLISECOND:
                    deviceOffset = offset * configuration.getBeatsPerMinute() * 1000;
                    break;
                case MICROSECOND:
                    deviceOffset = offset;
            }
            LOG.debug("Adding series {} with time units {}", series, series.getTimeUnits());
            seriesSet.add(new SeriesTimeUnitConverterFactory(
                    new TimeUnitConverter(TimeUnit.MICROSECOND, deviceOffset, configuration.getBeatsPerMinute()))
                    .convertSeries(series));
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
        if (seriesProcessor != null) {
            seriesProcessor.stop();
        }
    }
}
