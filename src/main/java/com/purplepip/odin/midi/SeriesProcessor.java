package com.purplepip.odin.midi;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;
import java.util.Set;

/**
 * Series processor.
 */
public class SeriesProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SeriesProcessor.class);

    private Set<Series<Note>> seriesSet;
    private MidiDevice device;
    private boolean exit;

    public SeriesProcessor(MidiDevice device, Set<Series<Note>> seriesSet) {
        this.seriesSet = seriesSet;
        this.device = device;
    }

    public void run() {
        while (!exit) {
            LOG.debug("Processing series");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted", e);
            }
        }
    }

    public void stop() {
        exit = true;
    }
}
