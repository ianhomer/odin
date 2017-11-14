package com.purplepip.odin.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.Test;

/**
 * Test on transient project.
 */
public class TransientPerformanceTest {
  private static final int EXPECTED_SEQUENCE_COUNT = 1;

  @Test
  public void testAddSequence() {
    Performance performance = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(performance));
    builder.addMetronome();
    int count = 0;
    SequenceConfiguration firstSequence = null;
    for (SequenceConfiguration sequence : performance.getSequences()) {
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
    Performance performance = new TransientPerformance();
    assertEquals("transient", performance.getName());
  }
}