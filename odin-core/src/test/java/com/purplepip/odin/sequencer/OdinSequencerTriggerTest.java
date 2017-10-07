package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.sequence.triggers.Action;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
public class OdinSequencerTriggerTest {
  private static final int OFFSET = 0;
  private static final int LENGTH = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    // TODO : Update this test so that the trigger actually enables to the sequence.
    new BeanyProjectBuilder(environment.getContainer())
        .withName("note-60-trigger")
        .withNote(60)
        .addNoteTrigger()
        .addLayer("groove")
        .withLayers("groove")
        .withTrigger("note-60-trigger", Action.ENABLE)
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .withActive(false)
        .addMetronome();
    environment.start();
    //environment.getConfiguration().getOperationTransmitter().send(
    //    new NoteOnOperation(0,60,5), -1
    //);
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
