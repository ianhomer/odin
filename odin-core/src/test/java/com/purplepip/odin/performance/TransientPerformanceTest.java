package com.purplepip.odin.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.purplepip.odin.creation.channel.DefaultChannel;
import com.purplepip.odin.creation.layer.DefaultLayer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
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

  @Test
  public void testMixin() {
    Performance mixinPerformance = new TransientPerformance();
    mixinPerformance.addChannel(new DefaultChannel(8).programName("channel-8"));
    mixinPerformance.addLayer(new DefaultLayer("layer-from-mixin"));
    mixinPerformance.addSequence(new Pattern().name("sequence-from-mixin"));
    mixinPerformance.addTrigger(new NoteTrigger().name("trigger-from-mixin"));

    Performance performance = new TransientPerformance();
    performance.addChannel(new DefaultChannel(9).programName("channel-9"));
    performance.addLayer(new DefaultLayer("my-layer"));
    performance.addSequence(new Pattern().name("my-sequence"));
    performance.addTrigger(new NoteTrigger().name("my-trigger"));

    performance.mixin(mixinPerformance);

    assertEquals(2, performance.getChannels().size());
    assertEquals(2, performance.getLayers().size());
    assertEquals(2, performance.getSequences().size());
    assertEquals(2, performance.getTriggers().size());
  }

  @Test
  public void testName() {
    assertEquals("transient", new TransientPerformance().getName());
    assertEquals("com/purplepip/odin/performance/"
        + "TransientPerformanceTest$ExtendedTransientPerformance",
        new ExtendedTransientPerformance().getName());
  }

  private static class ExtendedTransientPerformance extends TransientPerformance {
  }
}