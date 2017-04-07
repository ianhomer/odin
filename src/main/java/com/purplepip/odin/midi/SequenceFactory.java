package com.purplepip.odin.midi;

import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;

/**
 * Create sequences.
 */
public class SequenceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SequenceFactory.class);
    private long maxEventLength = 255;

    public Sequence createSequence(Series<Note> series) throws InvalidMidiDataException {
        LOG.debug("Creating sequence from series {}" , series);
        Sequence sequence = new Sequence(Sequence.PPQ, 1);
        Track track = sequence.createTrack();

        long eventCount = 0;
        while (series.peek() != null && eventCount < maxEventLength) {
            Event<Note> event = series.pop();
            LOG.debug("Adding event {}", event);

            addToTrack(track, event.getValue(), ShortMessage.NOTE_ON,  eventCount);
            addToTrack(track, event.getValue(), ShortMessage.NOTE_OFF,  eventCount + 1);
            eventCount++;
        }
        return sequence;
    }

    private void addToTrack(Track track, Note note, int command, long tick) throws InvalidMidiDataException {
        track.add(new MidiEvent(new ShortMessage(command, 1, note.getNumber(), note.getVelocity()), tick));
    }
}
