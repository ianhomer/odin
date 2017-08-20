package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
public class OdinSequencerTest {
  // TODO : After doing dynamic sequence bean creation in FlowFactory, start up slowed
  // down by about 20 ms, I changed OFFSET from 0 to 16 to give a window for this to occur
  // it'd be good to make this more robust, perhaps setting all environment up before starting
  // clock.
  private static final int OFFSET = 16;
  private static final int LENGTH = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new ProjectBuilder(environment.getContainer())
        .addLayer("groove")
        .withLayers("groove")
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
    Real currentBeat = environment.getSequencer().getClock().getPosition();
    assertTrue(currentBeat
        + " beats should not have past", currentBeat.lt(Whole.valueOf(OFFSET + LENGTH)));
  }
}
