package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.AbstractNoteVelocityOperation;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.tick.Ticks;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class NotationUpdatedAtRuntimeTest {
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
          LOG.warn("Unexpected note operation : " + operation);
        }
      } else {
        LOG.warn("Unexpected operation");
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new ProjectBuilder(environment.getContainer())
        .addNotation(Ticks.BEAT, "C");
    environment.start();
    OdinSequencerStatistics statistics = environment.getSequencer().getStatistics();
    Notation notation = (Notation) environment.getContainer().getSequences().iterator().next();

    try {
      note60Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough note 60 events fired", 0, note60Events.getCount());
      assertEquals("No note 61 notes should have been fired yet", 16, note61Events.getCount());
      LOG.debug("Updating pattern");
      notation.setNotation("C#");
      environment.getContainer().apply();
      note61Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough note 61 events fired", 0, note61Events.getCount());
    } finally {
      environment.stop();
    }

    assertEquals("Number of added tracks not correct", 1, statistics.getTrackAddedCount());
    assertEquals("Number of updated tracks not correct", 1, statistics.getTrackUpdatedCount());
    assertEquals("Number of removed tracks not correct", 0, statistics.getTrackRemovedCount());
  }
}
