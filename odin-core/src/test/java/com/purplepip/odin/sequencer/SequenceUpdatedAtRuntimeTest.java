package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class SequenceUpdatedAtRuntimeTest {
  private static final int OFFSET = 0;
  private static final int LENGTH = 8;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch channel0Events = new CountDownLatch(16);
    final CountDownLatch channel1Events = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof ChannelOperation) {
        ChannelOperation channelOperation = (ChannelOperation) operation;
        if (channelOperation.getChannel() == 0) {
          channel0Events.countDown();
        } else if (channelOperation.getChannel() == 1) {
          channel1Events.countDown();
        } else {
          LOG.warn("Unexpected channel operation");
        }
      } else {
        LOG.warn("Unexpected operation");
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    ProjectBuilder builder = new ProjectBuilder(environment.getContainer())
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    environment.start();

    try {
      channel0Events.await(1000, TimeUnit.MILLISECONDS);
      builder.withChannel(1).withLength(LENGTH).withOffset(LENGTH).addMetronome();
      environment.getContainer().apply();
      channel1Events.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    assertEquals("Not enough channel 0 events fired", 0, channel0Events.getCount());
  }
}
