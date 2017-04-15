package com.purplepip.odin.midi;

import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.series.*;
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
    private Set<SeriesTrack> seriesTrackSet = new HashSet<>();
    private SeriesProcessor seriesProcessor;
    private MidiMessageProcessor midiMessageProcessor;
    private SeriesTimeUnitConverterFactory seriesTimeUnitConverterFactory;
    private TickConverter deviceOffsetConverter;
    private Clock clock;
    private Meter meter;

    public OdinSequencer(OdinSequencerConfiguration configuration) throws MidiException {
        this.configuration = configuration;
        start();
    }

    private void init() throws MidiException, MidiUnavailableException {
        initDevice();
        initSynthesizer();
        if (configuration.isCoreJavaSequencerEnabled()) {
            initSequencer();
        }
        clock = new Clock(configuration.getBeatsPerMinute());
        clock.startAtNextSecond(device.getMicrosecondPosition());
        meter = new Meter(clock, configuration.getMeasureProvider());
        midiMessageProcessor = new MidiMessageProcessor(device);
        Thread thread = new Thread(midiMessageProcessor);
        thread.start();
        seriesProcessor = new SeriesProcessor(device, seriesTrackSet, midiMessageProcessor);
        thread = new Thread(seriesProcessor);
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

    private void initSynthesizer() {
        if ("Gervill".equals(device.getDeviceInfo().getName())) {
            LOG.debug("Initialising internal synthesizer");
            try {
                // TODO : Externalise configuration - 41 is strings in internal Java engine
                device.getReceiver().send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 41, 0),
                        -1);
            } catch (MidiUnavailableException | InvalidMidiDataException e) {
                LOG.error("Cannot change synthesizer instruments", e);
            }
        }
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

    public void addSeries(Series<Note> series, long offset) throws MidiException {
        addSeries(series, offset, 0);
    }

    /**
     * Add series at the given time offset, where offset is in the time units of the series being added.
     *
     * @param series
     * @param offset
     * @throws MidiException
     */
    public void addSeries(Series<Note> series, long offset, int channel) throws MidiException {
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
            LOG.debug("Adding series {} with time units {}", series, series.getTick());
            seriesTrackSet.add(new SeriesTrack(new SeriesTimeUnitConverterFactory(
                    new TickConverter(clock, series.getTick(), Tick.MICROSECOND, offset))
                    .convertSeries(series), channel));
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
        if (midiMessageProcessor != null) {
            midiMessageProcessor.stop();
        }
    }
}
