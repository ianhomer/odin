package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.demo.DemoLoaderPerformance;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.OperationReceiver;
import com.purplepip.odin.performance.LoadPerformanceOperation;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test match note use case odin sequencer.
 */
@Slf4j
public class OdinSequencerLoadPerformanceTest {
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch noteLatch = new CountDownLatch(4);
    final CountDownLatch loadPerformanceLatch = new CountDownLatch(1);

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        noteLatch.countDown();
      } else if (operation instanceof NoteOffOperation) {
        LOG.trace("Ignoring note off operation : {}", operation);
      } else if (operation instanceof LoadPerformanceOperation) {
        LOG.debug("Operation : {}", operation);
        loadPerformanceLatch.countDown();
      } else {
        LOG.warn("Unexpected operation : {}", operation);
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver,
        new DemoLoaderPerformance());
    environment.start();

    try {
      /*
       * Wait for a few notes to fire before sending note to trigger performance load
       */
      noteLatch.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Some notes should have been received", noteLatch.getCount(), 0);

      environment.getConfiguration().getOperationTransmitter().handle(
          new NoteOnOperation(1,50,5), -1
      );
      loadPerformanceLatch.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Load performance operation should have been received",
          loadPerformanceLatch.getCount(), 0);

    } finally {
      environment.stop();
    }
  }
}
