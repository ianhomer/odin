package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.tick.Ticks;
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
  @Test
  public void testComplexSequencer() throws OdinException, InterruptedException {
    final CountDownLatch lock = new CountDownLatch(50);

    OperationReceiver operationReceiver = (operation, time) -> {
      lock.countDown();
      LOG.debug("Received {}", operation);
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    new ProjectBuilder(environment.getContainer())
        .addLayer("groove").withLayers("groove")
        .addMetronome()
        .withChannel(1).withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
        .withChannel(2).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withChannel(8).withVelocity(100).withNote(42).addPattern(Ticks.BEAT, 15)
        .withChannel(9).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withVelocity(20)
        .addPattern(Ticks.EIGHTH, 127)
        .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

    environment.start();
    try {
      lock.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    assertEquals("Not enough events fired", 0, lock.getCount());
  }
}
