package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class SequenceUpdatedAtRuntimeTest {
  private static final int OFFSET = 0;
  private static final int LENGTH = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch channel0Events = new CountDownLatch(16);
    final CountDownLatch channel1Events = new CountDownLatch(16);
    final AtomicInteger programChangeEvents = new AtomicInteger();

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof ChannelOperation) {
        ChannelOperation channelOperation = (ChannelOperation) operation;
        if (channelOperation.getChannel() == 0) {
          channel0Events.countDown();
          LOG.debug("Channel 0 count : {}", channel0Events.getCount());
        } else if (channelOperation.getChannel() == 1) {
          channel1Events.countDown();
          LOG.debug("Channel 1 count : {}", channel1Events.getCount());
        } else {
          LOG.warn("Unexpected channel operation");
        }
        if (operation instanceof ProgramChangeOperation) {
          programChangeEvents.incrementAndGet();
          LOG.debug("Program change event : {} : {}", operation, programChangeEvents.get());
        }
      } else {
        LOG.warn("Unexpected operation");
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    ProjectBuilder builder = new ProjectBuilder(environment.getContainer())
        .changeProgramTo("violin")
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    environment.start();

    try {
      channel0Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough channel 0 events fired", 0, channel0Events.getCount());
      builder
          .withChannel(1).withLength(LENGTH).withOffset(OFFSET + LENGTH * 2).addMetronome()
          .withChannel(2).changeProgramTo("piano");
      environment.getContainer().apply();
      channel1Events.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    /*
     * Verify that only the explicit program changes are received and earlier ones are not repeated.
     */
    assertEquals("Incorrect number of program change events", 2, programChangeEvents.get());
    assertEquals("Not enough channel 1 events fired", 0, channel1Events.getCount());
  }
}
