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
    private boolean exit;
    // TODO : Externalise configuration
    private long refreshPeriod = 1000;
    private long timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
    private int maxNotesPerBuffer = 1000;

    public SeriesProcessor(MidiDevice device, Set<Series<Note>> seriesSet) {
        this.seriesSet = seriesSet;
        if (device == null) {
            throw new RuntimeException("Device must not be null");
        }
        this.device = device;
    }

    public void run() {
        while (!exit) {
            LOG.debug("Processing series");
            try {
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
                            LOG.debug("Processing Event {}", nextEvent);
                            if (nextEvent.getTime() < device.getMicrosecondPosition()) {
                                LOG.debug("Skipping event, too late to process {} < {}", nextEvent.getTime(),
                                        device.getMicrosecondPosition());
                            } else {
                                Note note = nextEvent.getValue();
                                LOG.debug("Sending note {} ; {}", note.getNumber(), note.getVelocity(), note.getDuration());
                                try {
                                    ShortMessage noteOn = new ShortMessage(ShortMessage.NOTE_ON, 1,
                                            note.getNumber(), note.getVelocity());
                                    ShortMessage noteOff = new ShortMessage(ShortMessage.NOTE_OFF, 1,
                                            note.getNumber());
//                                    MidiSystem.getReceiver().send(noteOn, -1);
                                    device.getReceiver().send(noteOn, nextEvent.getTime());
                                    device.getReceiver().send(noteOff, nextEvent.getTime() + note.getDuration());
                                } catch (InvalidMidiDataException e) {
                                    LOG.error("Cannot create note message {}", note);
                                } catch (MidiUnavailableException e) {
                                    LOG.error("Cannot send note {}", note);
                                }
                            }
                            noteCountThisBuffer++;
                            nextEvent = series.peek();
                            LOG.debug("Next event {}", nextEvent);
                        }
                    } else {
                        LOG.debug("No event on series");
                    }
                }
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
