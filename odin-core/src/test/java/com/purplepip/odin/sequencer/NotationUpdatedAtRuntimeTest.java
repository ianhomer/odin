package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.music.operations.AbstractNoteVelocityOperation;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
class NotationUpdatedAtRuntimeTest {
  @Test
  void testSequencer() throws InterruptedException {
    final CountDownLatch note60Events = new CountDownLatch(16);
    final CountDownLatch note61Events = new CountDownLatch(16);

    OperationHandler operationReceiver = (operation, time) -> {
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
    new PerformanceBuilder(environment.getContainer())
        .addLayer("groove").withLayers("groove")
        .withName("notation-updated")
        .addNotation(Ticks.BEAT, "C");
    environment.start();
    OdinSequencerStatistics statistics = environment.getSequencer().getStatistics();
    Notation notation = (Notation) environment.getContainer().getSequences().iterator().next();

    try {
      note60Events.await(3000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough note 60 events fired", 0, note60Events.getCount());
      assertEquals("No note 61 notes should have been fired yet", 16, note61Events.getCount());
      LOG.debug("Updating pattern");
      notation.setNotation("C#");
      /*
       * Move the clock a point in the future so that we can confirm that the sequencer scans
       * forward correctly
       */
      try (LogCaptor captor = new LogCapture().warn().from(Notation.class)
          .withPassThrough().start()) {
        // TODO : Increase the forward scan (setMicroseconds) of the clock, since system
        // should be robust to this.
        environment.getSequencer().getClock().setMicroseconds(1000);
        environment.getContainer().apply();
        note61Events.await(5000, TimeUnit.MILLISECONDS);
        if (captor.hasMessages()) {
          for (int i = 0; i < captor.size(); i++) {
            LOG.error("Captured WARN message {}", captor.getMessage(i));
          }
        }
        assertEquals("No WARN messages should have been logged.",0, captor.size());
      }

      assertEquals("Not enough note 61 events fired", 0, note61Events.getCount());
    } finally {
      environment.shutdown();
    }

    assertEquals("Number of added tracks not correct", 1,
        statistics.getTrackStatistics().getAddedCount());
    assertEquals("Number of updated tracks not correct", 1,
        statistics.getTrackStatistics().getUpdatedCount());
    assertEquals("Number of removed tracks not correct", 0,
        statistics.getTrackStatistics().getRemovedCount());
  }
}
