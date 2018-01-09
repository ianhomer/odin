package com.purplepip.odin.sequencer;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.AbstractNoteVelocityOperation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.operation.OperationReceiver;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class SequenceTickUpdatedAtRuntimeTest {
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch note60Events = new CountDownLatch(16);
    final CountDownLatch note61Events = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof AbstractNoteVelocityOperation) {
        AbstractNoteVelocityOperation noteVelocityOperation =
            (AbstractNoteVelocityOperation) operation;
        if (noteVelocityOperation.getNumber() == 60) {
          note60Events.countDown();
          LOG.debug("Note 60 count : {}", note60Events.getCount());
        } else if (noteVelocityOperation.getNumber() == 61) {
          note61Events.countDown();
          LOG.debug("Note 61 count : {}", note61Events.getCount());
        } else {
          LOG.warn("Unexpected note operation");
        }
      } else {
        LOG.warn("Unexpected operation");
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new PerformanceBuilder(environment.getContainer())
        .addLayer("groove").withLayers("groove")
        .withNote(60)
        .addPattern(Ticks.EIGHTH, 1);
    environment.start();
    OdinSequencerStatistics statistics = environment.getSequencer().getStatistics();
    Pattern pattern = (Pattern) environment.getContainer().getSequences().iterator().next();

    try {
      note60Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough note 60 events fired", 0, note60Events.getCount());
      assertEquals("No note 61 notes should have been fired yet", 16, note61Events.getCount());
      LOG.debug("Updating pattern");
      Note note = pattern.getNote();
      pattern.setNote(new DefaultNote(61, note.getVelocity(), note.getDuration()));
      pattern.setTick(Ticks.THIRD);
      environment.getContainer().apply();
      note61Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough note 61 events fired", 0, note61Events.getCount());
    } finally {
      environment.stop();
    }

    // TODO : Get this skipped count down to 0, as if it's non-zero then it's indicative something
    // is odd.
    assertTrue("No events should have been skipped", statistics.getEventTooLateCount() < 3);
    assertEquals("Number of added tracks not correct", 1,
        statistics.getTrackStatistics().getAddedCount());
    assertEquals("Number of updated tracks not correct", 1,
        statistics.getTrackStatistics().getUpdatedCount());
    assertEquals("Number of removed tracks not correct", 0,
        statistics.getTrackStatistics().getRemovedCount());
  }
}
