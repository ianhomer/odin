package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
public class OdinSequencerTest {
  private static final int OFFSET = 8;
  private static final int LENGTH = 8;


  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new ProjectBuilder(sequencer.getProject())
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    sequencer.start();

    try {
      lock.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      sequencer.stop();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
    double currentBeat = sequencer.getClock().getCurrentBeat();
    assertTrue(currentBeat
        + " beats should not have past", sequencer.getClock().getCurrentBeat()
        < OFFSET + LENGTH);
  }
}
