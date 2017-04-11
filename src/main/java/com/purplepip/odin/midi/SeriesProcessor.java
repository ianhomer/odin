package com.purplepip.odin.midi;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.Set;

/**
 * Series processor.
 */
public class SeriesProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SeriesProcessor.class);

    private Set<Series<Note>> seriesSet;
    private MidiDevice device;
    private MidiMessageProcessor midiMessageProcessor;
    private boolean exit;
    // TODO : Externalise configuration
    private long refreshPeriod = 200;
    private long timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
    private int maxNotesPerBuffer = 1000;

    public SeriesProcessor(MidiDevice device, Set<Series<Note>> seriesSet, MidiMessageProcessor midiMessageProcessor) {
        this.seriesSet = seriesSet;
        if (device == null) {
            throw new RuntimeException("Device must not be null");
        }
        this.device = device;
        this.midiMessageProcessor = midiMessageProcessor;
    }

    public void run() {
        while (!exit) {
            LOG.debug("Processing series for device at position {}", device.getMicrosecondPosition());
            int noteCountThisBuffer = 0;
            for (Series<Note> series : seriesSet) {
                if (noteCountThisBuffer > maxNotesPerBuffer) {
                    LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer, maxNotesPerBuffer);
                    break;
                }
                if (series.peek() != null) {
                    Event<Note> nextEvent = series.peek();
                    while (nextEvent != null && nextEvent.getTime() < device.getMicrosecondPosition() + timeBufferInMicroSeconds) {
                        if (noteCountThisBuffer > maxNotesPerBuffer) {
                            LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer, maxNotesPerBuffer);
                            break;
                        }
                        /*
                         * Pop event to get it off the buffer.
                         */
                        nextEvent = series.pop();
                        LOG.trace("Processing Event {}", nextEvent);
                        if (nextEvent.getTime() < device.getMicrosecondPosition()) {
                            LOG.debug("Skipping event, too late to process {} < {}", nextEvent.getTime(),
                                    device.getMicrosecondPosition());
                        } else {
                            Note note = nextEvent.getValue();
                            LOG.debug("Sending note {} ; {} ; {}", note.getNumber(), note.getVelocity(), nextEvent.getTime());
                            try {
                                ShortMessage noteOn = new ShortMessage(ShortMessage.NOTE_ON, 1,
                                        note.getNumber(), note.getVelocity());
                                ShortMessage noteOff = new ShortMessage(ShortMessage.NOTE_OFF, 1,
                                        note.getNumber(), note.getVelocity());
                                midiMessageProcessor.send(noteOn, nextEvent.getTime());
                                midiMessageProcessor.send(noteOff, nextEvent.getTime() + note.getDuration());
                            } catch (InvalidMidiDataException e) {
                                LOG.error("Cannot create note message {}", note);
                            }
                        }
                        noteCountThisBuffer++;
                        nextEvent = series.peek();
                        LOG.trace("Next event {}", nextEvent);
                    }
                } else {
                    LOG.debug("No event on series");
                }
            }
            try {
                Thread.sleep(refreshPeriod);
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted", e);
            }
        }
    }

    public void stop() {
        exit = true;
    }
}
