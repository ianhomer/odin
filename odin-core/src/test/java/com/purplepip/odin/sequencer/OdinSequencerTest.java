package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.operation.OperationHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Test odin sequencer.
 */
class OdinSequencerTest {
  private static final int OFFSET = 100;
  private static final int LENGTH = 16;

  @Test
  void testSequencer() throws InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationHandler operationReceiver = (operation, time) -> lock.countDown();

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new BeanyPerformanceBuilder(environment.getContainer())
        .addLayer("groove")
        .withLayers("groove")
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    environment.start();

    try {
      lock.await(2000, TimeUnit.MILLISECONDS);
    } finally {
      environment.shutdown();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
    Real currentBeat = environment.getSequencer().getClock().getPosition();
    assertTrue(currentBeat
        + " beats should not have past", currentBeat.lt(Wholes.valueOf(OFFSET + LENGTH)));
  }
}
