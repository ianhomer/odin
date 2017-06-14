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
  private static final int OFFSET = 0;
  private static final int LENGTH = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new ProjectBuilder(environment.getContainer())
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    environment.start();

    try {
      lock.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
    double currentBeat = environment.getSequencer().getClock().getCurrentBeat();
    assertTrue(currentBeat
        + " beats should not have past", currentBeat < OFFSET + LENGTH);
  }
}
