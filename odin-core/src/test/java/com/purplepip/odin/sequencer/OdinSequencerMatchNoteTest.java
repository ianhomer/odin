package com.purplepip.odin.sequencer;

import static com.purplepip.odin.sequence.layer.Layers.newLayer;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.triggers.Action;
import com.purplepip.odin.sequence.triggers.NoteTrigger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test match note use case odin sequencer.
 */
public class OdinSequencerMatchNoteTest {
  private static final int OFFSET = 0;
  private static final int LENGTH = -1;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> lock.countDown();

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    environment.getContainer()
        .addTrigger(new NoteTrigger().note(60).name("note-60-trigger"))
        .addLayer(newLayer("groove"))
        .addSequence((Metronome) new Metronome()
            .layer("groove")
            .trigger("note-60-trigger", Action.ENABLE)
            .offset(OFFSET)
            .length(LENGTH)
            .enabled(false)
            .name("metronome"));

    environment.start();

    // TODO : Change this test case to cover the match the note use case.
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
