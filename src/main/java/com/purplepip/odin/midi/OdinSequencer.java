package com.purplepip.odin.midi;

import com.purplepip.odin.OdinException;
import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationProcessor;
import com.purplepip.odin.sequencer.SeriesProcessor;
import com.purplepip.odin.sequencer.SeriesTrack;
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
    private OperationProcessor operationProcessor;
    private Clock clock;
    private Meter meter;

    public OdinSequencer(OdinSequencerConfiguration configuration) throws OdinException {
        this.configuration = configuration;
        try {
            init();
        } catch (MidiUnavailableException e) {
            throw new OdinException(e);
        }
    }

    private void init() throws OdinException, MidiUnavailableException {
        initDevice();
        clock = new Clock(configuration.getBeatsPerMinute());
        clock.start(new MidiDeviceMicrosecondPositionProvider(device), true);
        initSynthesizer();
        meter = new Meter(clock, configuration.getMeasureProvider());
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

    private void initDevice() throws OdinException {
        // TODO : Externalise and prioritise external MIDI devices to connect to.
        device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
        if (device == null) {
            device = new MidiSystemHelper().findMidiDeviceByName("Gervill");
        }
        LOG.debug("MIDI device : {}", device);
    }

    public void addSeries(Series<Note> series, long offset) throws OdinException {
        addSeries(series, offset, 0);
    }

    /**
     * Add series at the given time offset, where offset is in the time units of the series being added.
     *
     * @param series
     * @param offset
     * @throws OdinException
     */
    public void addSeries(Series<Note> series, long offset, int channel) throws OdinException {
        LOG.debug("Adding series {} with time units {}", series, series.getTick());
        seriesTrackSet.add(new SeriesTrack(new SeriesTimeUnitConverterFactory(
                new DefaultTickConverter(clock, series.getTick(), Tick.MICROSECOND, offset))
                .convertSeries(series), channel));
    }


    public void start() throws OdinException {
        operationProcessor = new MidiOperationProcessor(clock, device);
        Thread thread = new Thread(operationProcessor);
        thread.start();
        seriesProcessor = new SeriesProcessor(clock, seriesTrackSet, operationProcessor);
        thread = new Thread(seriesProcessor);
        thread.start();
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
        if (operationProcessor != null) {
            operationProcessor.stop();
        }
    }
}
