package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.BeatPerformance;
import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class OdinSequencerReloadTest {
  private static final int OFFSET = 100;
  private static final int LENGTH = 16;
  private static final int EXPECTED_COUNT = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch channel1Latch = new CountDownLatch(2);
    final CountDownLatch channel9Latch = new CountDownLatch(EXPECTED_COUNT);

    PerformanceContainer container = new PerformanceContainer(new SimplePerformance());

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation = (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          channel1Latch.countDown();
        } else if (noteOnOperation.getChannel() == 9) {
          channel9Latch.countDown();
        } else {
          LOG.warn("Unexpected note operation : {}", operation);
        }
      } else if (operation instanceof LoadPerformanceOperation) {
        String performanceName = ((LoadPerformanceOperation) operation).getPerformanceName();
        try {
          container.setPerformance((Performance)
              getClass().getClassLoader().loadClass(performanceName).newInstance());
          LOG.info("Loaded {}", performanceName);
          container.apply();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
          LOG.error("Cannot load " + performanceName,e);
        }
      } else if (operation instanceof NoteOffOperation) {
        LOG.trace("Ignored {} : ", operation);
      } else {
        LOG.warn("Unexpected operation : {}", operation);
      }

    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver,
        container);
    environment.start();

    try {
      channel1Latch.await(2000, TimeUnit.MILLISECONDS);

      assertEquals("Not enough channel 1 events fired", 0, channel1Latch.getCount());
      assertEquals("No channel 9 events should be fired yet", EXPECTED_COUNT,
          channel9Latch.getCount());

      environment.getConfiguration().getOperationTransmitter().send(
          new LoadPerformanceOperation(BeatPerformance.class.getName()), -1
      );

      channel9Latch.await(2000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough channel 9 events fired", 0, channel9Latch.getCount());

    } finally {
      environment.stop();
    }

  }
}
