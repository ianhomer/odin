package com.purplepip.odin.project;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.Sequence;
import org.junit.Test;

/**
 * Test on transient project.
 */
public class TransientProjectTest {
  private static final int EXPECTED_SEQUENCE_COUNT = 1;

  @Test
  public void testAddSequence() {
    Sequence<Note> pattern = new Metronome();
    Project project = new TransientProject();
    project.addSequence(pattern);
    int count = 0;
    Sequence<Note> sequence1 = null;
    for (Sequence<Note> sequence : project.getSequences()) {
      if (count == 0) {
        sequence1 = sequence;
      }
      count++;
    }
    assertEquals("Expected sequence count not correct", EXPECTED_SEQUENCE_COUNT, count);
    assertEquals("First sequence not as expected", pattern, sequence1);
  }
}