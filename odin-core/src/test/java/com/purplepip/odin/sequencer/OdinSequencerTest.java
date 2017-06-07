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
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new ProjectBuilder(sequencer.getProject())
        .withLength(8)
        .addMetronome();
    sequencer.start();

    try {
      lock.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      sequencer.stop();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
    assertTrue("8 beats should not have past", sequencer.getClock().getCurrentBeat() < 8);
  }
}
