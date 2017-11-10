package com.purplepip.odin.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.purplepip.odin.composition.SequenceConfiguration;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

/**
 * Test on transient project.
 */
public class TransientProjectTest {
  private static final int EXPECTED_SEQUENCE_COUNT = 1;

  @Test
  public void testAddSequence() {
    Project project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addMetronome();
    int count = 0;
    SequenceConfiguration firstSequence = null;
    for (SequenceConfiguration sequence : project.getSequences()) {
      if (count == 0) {
        firstSequence = sequence;
      }
      count++;
    }
    assertEquals("Expected sequence count not correct", EXPECTED_SEQUENCE_COUNT, count);
    assertNotNull(firstSequence);
    assertEquals("First sequence not as expected", Metronome.class,
        firstSequence.getClass());
  }

  @Test
  public void testGetName() {
    Project project = new TransientProject();
    assertEquals("transient", project.getName());
  }
}