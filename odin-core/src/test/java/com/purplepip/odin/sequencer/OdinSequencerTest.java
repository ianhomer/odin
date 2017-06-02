package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.Tick;

import com.purplepip.odin.sequence.Ticks;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test odin sequencer.
 */
public class OdinSequencerTest {
  private static final Logger LOG = LoggerFactory.getLogger(OdinSequencerTest.class);

  @Test
  public void testSequencer() throws OdinException {
    CapturingOperationReceiver operationReceiver = new CapturingOperationReceiver();
    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new SequenceBuilder(sequencer.getProject())
        .withLength(8)
        .addMetronome();
    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 12) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    sequencer.stop();
    LOG.info("... stopped");

    assertEquals("Number of operations sent not correct", 16,
        operationReceiver.getList().size());
  }
}
