package com.purplepip.odin.midi;

import com.purplepip.odin.music.Metronome;
import com.purplepip.odin.music.StaticMeasureProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.Assert.assertEquals;

/**
 * Sequence Factory Test.
 */
public class SequenceFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SequenceFactory.class);

    @Test
    public void testCreateSequence() throws InvalidMidiDataException {
        Sequence sequence = new SequenceFactory().createSequence(new Metronome(new StaticMeasureProvider(4), 4));
        assertEquals("One track expected in sequence", 1, sequence.getTracks().length);
        /*
         * 9 messages ; 4 x on and off and then a find end of track message
         */
        assertEquals("Track 1 should have 9 messages", 9, sequence.getTracks()[0].size());
    }
}