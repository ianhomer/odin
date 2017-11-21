package com.purplepip.odin.sequencer;

import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.sequence.Action;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test match note use case odin sequencer.
 */
@Slf4j
public class OdinSequencerMatchNoteTest {
  private static final List<Integer> SUCCESS_NOTES = Arrays.asList(60, 62, 64);

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch randomNoteLatch = new CountDownLatch(1);
    final AtomicInteger randomNote = new AtomicInteger();
    final CountDownLatch successEventsLatch = new CountDownLatch(SUCCESS_NOTES.size());
    final ArrayList<Integer> successNotes = new ArrayList<>();

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation =
            (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          randomNote.set(noteOnOperation.getNumber());
          randomNoteLatch.countDown();
        } else if (noteOnOperation.getChannel() == 2) {
          if (successEventsLatch.getCount() > 0) {
            successNotes.add(noteOnOperation.getNumber());
          }
          successEventsLatch.countDown();
        } else {
          LOG.warn("Unexpected note operation : {}", noteOnOperation);
        }
      } else if (operation instanceof NoteOffOperation) {
        LOG.warn("Ignoring note off operation : {}", operation);
      } else {
        LOG.warn("Unexpected operation : {}", operation);
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    environment.getContainer()
        .addLayer(newLayer("groove"))
        .addSequence(new Random()
            .lower(60).upper(72)
            .bits(1).note(newNote())
            .channel(1).layer("groove")
            .name("random"))
        .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger"))
        .addSequence(new Notation()
            .notation("C D E")
            .trigger("random-note-trigger", Action.ENABLE)
            .channel(2).layer("groove")
            .enabled(false)
            .name("success"));

    environment.start();

    // TODO : Change this test case to cover the match the note use case.  Listen for note from
    // random note sequence, hit that note and then expect notation sequence.
    try {
      randomNoteLatch.await(1000, TimeUnit.MILLISECONDS);

      /*
       * Confirm success sequence does not fire on no activity.
       */
      successEventsLatch.await(100, TimeUnit.MILLISECONDS);
      assertEquals("Success sequence should not have fired before matching note pressed",
          successEventsLatch.getCount(), SUCCESS_NOTES.size());

      /*
       * Confirm success sequence does not fire with wrong note
       */
      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(0,randomNote.get() - 1,5), -1
      );
      successEventsLatch.await(100, TimeUnit.MILLISECONDS);
      assertEquals("Success sequence should not have fired after wrote note pressed",
          successEventsLatch.getCount(), SUCCESS_NOTES.size());

      /*
       * Confirm success sequence fires with correct note
       */
      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(0,randomNote.get(),5), -1
      );
      successEventsLatch.await(1000,TimeUnit.MILLISECONDS);

      /*
       * We'll check that at least the expected number of events have fired.
       */
      assertEquals("Success notes should have fired after correct note",
          0, successEventsLatch.getCount());
      // TODO : Assert that the success note are in the correct order
      //assertEquals("Success notes not correct", SUCCESS_NOTES, successNotes);


    } finally {
      environment.stop();
    }
  }
}
