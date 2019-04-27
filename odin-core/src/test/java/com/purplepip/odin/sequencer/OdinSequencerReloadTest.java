package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.BeatPerformance;
import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.ClassPerformanceLoader;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
class OdinSequencerReloadTest {
  private static final int EXPECTED_COUNT = 16;

  @Test
  void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch channel1Latch = new CountDownLatch(2);
    final CountDownLatch channel9Latch = new CountDownLatch(EXPECTED_COUNT);

    OperationHandler operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation = (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          channel1Latch.countDown();
        } else if (noteOnOperation.getChannel() == 9) {
          channel9Latch.countDown();
        } else {
          LOG.warn("Unexpected note operation : {}", operation);
        }
      } else if (operation instanceof NoteOffOperation
          || operation instanceof ProgramChangeOperation
          || operation instanceof LoadPerformanceOperation) {
        LOG.trace("Ignored {} : ", operation);
      } else {
        LOG.warn("Unexpected operation : {}", operation);
      }
    };

    DefaultPerformanceContainer container =
        new DefaultPerformanceContainer(new SimplePerformance());
    TestSequencerEnvironment environment = new TestSequencerEnvironment(
        new OperationReceiverCollection(operationReceiver,
            new ClassPerformanceLoader(new BeatPerformance(), container)),
        container);
    environment.start();

    try {
      channel1Latch.await(2000, TimeUnit.MILLISECONDS);

      assertEquals("Not enough channel 1 events fired", 0, channel1Latch.getCount());
      assertEquals("No channel 9 events should be fired yet", EXPECTED_COUNT,
          channel9Latch.getCount());

      environment.getConfiguration().getOperationTransmitter().handle(
          new LoadPerformanceOperation(
              new ClassUri(BeatPerformance.class, true).getUri()), -1
      );

      channel9Latch.await(2000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough channel 9 events fired", 0, channel9Latch.getCount());

    } finally {
      environment.shutdown();
    }

  }
}
