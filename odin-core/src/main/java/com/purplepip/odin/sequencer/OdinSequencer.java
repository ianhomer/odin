package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Core Odin Sequencer.
 */
public class OdinSequencer {
    private static final Logger LOG = LoggerFactory.getLogger(OdinSequencer.class);

    private OdinSequencerConfiguration configuration;
    private Set<SeriesTrack> seriesTrackSet = new HashSet<>();
    private SeriesProcessor seriesProcessor;
    private OperationProcessor operationProcessor;
    private Clock clock;
    private Meter meter;

    public OdinSequencer(OdinSequencerConfiguration configuration) throws OdinException {
        this.configuration = configuration;
        init();
    }

    private void init() throws OdinException {
        clock = new Clock(configuration.getBeatsPerMinute());
        clock.start(configuration.getMicrosecondPositionProvider(), true);
        meter = new Meter(clock, configuration.getMeasureProvider());
    }

    public void addSeries(Series<Note> series, long offset) throws OdinException {
        addSeries(series, offset, 0);
    }

    /**
     * Add series at the given time offset, where offset is in the time units of the series being added.
     *
     * @param series series to add.
     * @param offset offset to add the series to.
     * @throws OdinException exception
     */
    public void addSeries(Series<Note> series, long offset, int channel) throws OdinException {
        LOG.debug("Adding series {} with time units {}", series, series.getTick());
        seriesTrackSet.add(new SeriesTrack(new SeriesTimeUnitConverterFactory(
                new DefaultTickConverter(clock, series.getTick(), Tick.MICROSECOND, offset))
                .convertSeries(series), channel));
    }


    public void start() throws OdinException {
        operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
        Thread thread = new Thread(operationProcessor);
        thread.start();
        seriesProcessor = new SeriesProcessor(clock, seriesTrackSet, operationProcessor);
        thread = new Thread(seriesProcessor);
        thread.start();
    }

    public void stop() {
        if (seriesProcessor != null) {
            seriesProcessor.stop();
        }
        if (operationProcessor != null) {
            operationProcessor.stop();
        }
    }
}
