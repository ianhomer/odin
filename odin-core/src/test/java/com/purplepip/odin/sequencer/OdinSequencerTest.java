package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import org.junit.Test;


/**
 * Test odin sequencer.
 */
public class OdinSequencerTest {
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

    assertEquals("Number of operations sent not correct", 16,
        operationReceiver.getList().size());
  }
}
