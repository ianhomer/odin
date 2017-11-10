package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.composition.triggers.Action;
import com.purplepip.odin.music.operations.NoteOnOperation;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
public class OdinSequencerTriggerTest {
  private static final int OFFSET = 100;
  private static final int LENGTH = -1;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> lock.countDown();

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new BeanyProjectBuilder(environment.getContainer())
        .withName("note-60-trigger")
        .withNote(60)
        .addNoteTrigger()
        .addLayer("groove")
        .withName("metronome")
        .withLayers("groove")
        .withTrigger("note-60-trigger", Action.ENABLE)
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .withEnabled(false)
        .addMetronome();
    environment.start();
    environment.getConfiguration().getOperationTransmitter().send(
        new NoteOnOperation(0,60,5), -1
    );
    try {
      lock.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    /*
     * We'll check that at least the expected number of events have fired.
     */
    assertEquals("Not enough events fired", 0, lock.getCount());
  }
}
