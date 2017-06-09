package com.purplepip.odin.project;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.sequence.DefaultMetronome;
import com.purplepip.odin.sequence.Sequence;
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
    Sequence sequence1 = null;
    for (Sequence sequence : project.getSequences()) {
      if (count == 0) {
        sequence1 = sequence;
      }
      count++;
    }
    assertEquals("Expected sequence count not correct", EXPECTED_SEQUENCE_COUNT, count);
    assertEquals("First sequence not as expected", DefaultMetronome.class, sequence1.getClass());
  }

  @Test
  public void testGetName() {
    Project project = new TransientProject();
    assertEquals("transient", project.getName());
  }
}