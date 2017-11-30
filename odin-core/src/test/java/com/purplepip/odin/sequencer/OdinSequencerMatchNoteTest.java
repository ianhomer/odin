package com.purplepip.odin.sequencer;

import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionOperation;
import com.purplepip.odin.creation.action.IncrementAction;
import com.purplepip.odin.creation.action.InitialiseAction;
import com.purplepip.odin.creation.action.SetAction;
import com.purplepip.odin.creation.action.StartAction;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.SequenceStartTrigger;
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
  private static final List<Integer> SUCCESS_NOTES = Arrays.asList(60, 62, 64, 65);

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch randomNoteLatch2 = new CountDownLatch(1);
    final CountDownLatch randomNoteLatch3 = new CountDownLatch(1);
    final AtomicInteger randomNote2 = new AtomicInteger();
    final AtomicInteger randomNote3 = new AtomicInteger();
    final CountDownLatch successEventsLatch6 = new CountDownLatch(SUCCESS_NOTES.size());
    final CountDownLatch successEventsLatch7 = new CountDownLatch(SUCCESS_NOTES.size());
    final ArrayList<Integer> successNotes6 = new ArrayList<>();
    final ArrayList<Integer> successNotes7 = new ArrayList<>();
    final CountDownLatch startTrackLatch = new CountDownLatch(1);
    final CountDownLatch initialiseTrackLatch = new CountDownLatch(1);
    final CountDownLatch setTrackLatch = new CountDownLatch(1);


    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation =
            (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          // Input channel
        } else if (noteOnOperation.getChannel() == 2) {
          randomNote2.set(noteOnOperation.getNumber());
          randomNoteLatch2.countDown();
        } else if (noteOnOperation.getChannel() == 3) {
          randomNote3.set(noteOnOperation.getNumber());
          randomNoteLatch3.countDown();
        } else if (noteOnOperation.getChannel() == 6) {
          if (successEventsLatch6.getCount() > 0) {
            successNotes6.add(noteOnOperation.getNumber());
          }
          successEventsLatch6.countDown();
        } else if (noteOnOperation.getChannel() == 7) {
          if (successEventsLatch7.getCount() > 0) {
            successNotes7.add(noteOnOperation.getNumber());
          }
          successEventsLatch7.countDown();
        } else {
          LOG.warn("Unexpected note on operation : {}", noteOnOperation);
        }
      } else if (operation instanceof NoteOffOperation) {
        LOG.trace("Ignoring note off operation : {}", operation);
      } else if (operation instanceof ActionOperation) {
        Action action = ((ActionOperation) operation).getAction();
        if (action instanceof InitialiseAction) {
          initialiseTrackLatch.countDown();
        } else if (action instanceof  StartAction) {
          startTrackLatch.countDown();
        } else if (action instanceof SetAction) {
          setTrackLatch.countDown();
        } else {
          LOG.warn("Unexpected action operation : {}", operation);
        }
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
            // TODO : It'd be useful for a single trigger to be able to fire multiple actions
            .trigger("success-start-trigger", new SetAction().nameValuePairs("channel=3"))
            .trigger("success-start-trigger-2", new InitialiseAction())
            .trigger("success-start-trigger-3", new StartAction())
            .length(4)
            .channel(2).layer("groove")
            .name("random"))
        .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger"))
        .addTrigger(new PatternNoteTrigger().patternName("random").name("random-note-trigger-2"))
        .addTrigger(new SequenceStartTrigger()
            .sequenceName("success").name("success-start-trigger"))
        .addTrigger(new SequenceStartTrigger()
            .sequenceName("success").name("success-start-trigger-2"))
        .addTrigger(new SequenceStartTrigger()
            .sequenceName("success").name("success-start-trigger-3"))
        .addSequence(new Notation()
            .notation("C D E F")
            .trigger("random-note-trigger",
                new IncrementAction().propertyName("channel").increment(1))
            .trigger("random-note-trigger-2", new StartAction())
            .channel(5).layer("groove")
            .length(4)
            .enabled(false)
            .name("success"));

    environment.start();

    try {
      /*
       * Wait for a random note to be pressed so that we can get the value of the random note
       */
      randomNoteLatch2.await(1000, TimeUnit.MILLISECONDS);

      /*
       * Confirm success sequence does not fire on no activity.
       */
      successEventsLatch6.await(100, TimeUnit.MILLISECONDS);
      assertEquals("Success sequence should NOT have fired on channel 6 before matching "
              + " note pressed",
          successEventsLatch6.getCount(), SUCCESS_NOTES.size());

      /*
       * Confirm success sequence does not fire with wrong note
       */
      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(1,randomNote2.get() - 1,5), -1
      );
      successEventsLatch6.await(100, TimeUnit.MILLISECONDS);
      assertEquals("Success sequence should NOT have been fired on channel 6 "
              + " after wrong note pressed",
          successEventsLatch6.getCount(), SUCCESS_NOTES.size());

      int number2 = randomNote2.get();
      LOG.debug("random note on channel 2 = {}", number2);

      /*
       * Confirm success sequence fires with correct note
       */
      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(1, number2, 5), -1
      );
      successEventsLatch6.await(1000, TimeUnit.MILLISECONDS);

      /*
       * We'll check that at least the expected number of events have fired.
       */
      assertEquals("Success notes should have fired on channel 6 after correct note",
          0, successEventsLatch6.getCount());
      assertEquals("Success notes on channel 6 not correct", SUCCESS_NOTES, successNotes6);

      startTrackLatch.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Start track operation not fired",
          0, startTrackLatch.getCount());

      initialiseTrackLatch.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Reset track operation not fired",
          0, initialiseTrackLatch.getCount());

      initialiseTrackLatch.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Set track operation not fired",
          0, setTrackLatch.getCount());

      /*
       * Wait for a random note on channel 3
       */
      randomNoteLatch3.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Random note should have been fired on channel 3 after first success",
          0, randomNoteLatch3.getCount());

      int number3 = randomNote3.get();
      LOG.debug("random note on channel 3 = {}", number3);

      /*
       * Confirm success sequence fires with correct note
       */
      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(1, number3, 5), -1
      );
      successEventsLatch7.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Success notes on channel 7 not correct", SUCCESS_NOTES, successNotes7);

    } finally {
      environment.stop();
    }
  }
}
