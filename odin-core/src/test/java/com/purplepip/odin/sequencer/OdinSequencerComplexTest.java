package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.operation.OperationHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Complex sequencer test.  This is in a separate class to OdinSequencerTest to make
 * troubleshooting on failure easier.
 */
@Slf4j
public class OdinSequencerComplexTest {
  private static final int OFFSET = 100;
  private static final int LENGTH = 16;

  @Test
  public void testComplexSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(50);

    OperationHandler operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.debug("Received {}", operation);
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new BeanyPerformanceBuilder(environment.getContainer())
        .addLayer("verse").addLayer("chorus")
        .withLayers("verse", "chorus").addLayer("groove").withLayers("groove")
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .withName("metronome")
        .addMetronome()
        .withName("pattern1")
        .withChannel(1).withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
        .withName("pattern2")
        .withChannel(2).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withName("pattern3")
        .withChannel(8).withVelocity(100).withNote(42).addPattern(Ticks.BEAT, 15)
        .withName("pattern4")
        .withChannel(9).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withName("pattern5")
        .withVelocity(20)
        .addPattern(Ticks.EIGHTH, 127)
        .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

    environment.start();
    try {
      lock.await(5000, TimeUnit.MILLISECONDS);
    } finally {
      environment.shutdown();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
  }
}
