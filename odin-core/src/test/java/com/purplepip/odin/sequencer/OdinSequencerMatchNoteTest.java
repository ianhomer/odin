package com.purplepip.odin.sequencer;

import static com.purplepip.odin.music.notes.Notes.newNote;
import static com.purplepip.odin.sequence.layer.Layers.newLayer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Random;
import com.purplepip.odin.sequence.triggers.Action;
import com.purplepip.odin.sequence.triggers.PatternNoteTrigger;
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
        .addLayer(newLayer("groove"))
        .addSequence((Random) new Random()
            .lower(60).upper(72)
            .bits(1).note(newNote())
            .channel(1).layer("groove")
            .name("random"))
        .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger"))
        .addSequence((Notation) new Notation()
            .notation("C D E")
            .trigger("random-note-trigger", Action.ENABLE)
            .channel(1).layer("groove")
            .name("success"));

    // TODO : Start environment and continue test.  Need to find a way of trigger accessing
    // sequence configuration via Unmodifiable track
    //environment.start();


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
    // TODO : Check that C D E was called
  }
}
