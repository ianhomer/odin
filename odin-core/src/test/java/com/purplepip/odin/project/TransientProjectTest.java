package com.purplepip.odin.project;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    ProjectBuilder builder = new ProjectBuilder(project);
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
  public void testListener() {
    ProjectListener listener = mock(ProjectListener.class);
    Project project = new TransientProject();
    project.addListener(listener);
    project.apply();
    project.apply();
    verify(listener, times(2)).onProjectApply();
    project.removeListener(listener);
    verify(listener, times(2)).onProjectApply();
  }
}