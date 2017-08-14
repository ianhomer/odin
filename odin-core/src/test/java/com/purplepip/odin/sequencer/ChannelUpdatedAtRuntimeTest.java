package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.statistics.OdinSequencerStatistics;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Test odin sequencer.
 */
@Slf4j
public class ChannelUpdatedAtRuntimeTest {
  private static final int OFFSET = 0;
  private static final int LENGTH = 16;

  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch channel0Events = new CountDownLatch(16);
    final CountDownLatch channel1Events = new CountDownLatch(16);
    final CountDownLatch channel3Events = new CountDownLatch(16);
    final AtomicInteger programChangeEventCount = new AtomicInteger();

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof ChannelOperation) {
        ChannelOperation channelOperation = (ChannelOperation) operation;
        if (channelOperation.getChannel() == 0) {
          channel0Events.countDown();
          LOG.debug("Channel 0 count : {}", channel0Events.getCount());
        } else if (channelOperation.getChannel() == 1) {
          channel1Events.countDown();
          LOG.debug("Channel 1 count : {}", channel1Events.getCount());
        } else if (channelOperation.getChannel() == 3) {
          channel3Events.countDown();
        } else {
          LOG.warn("Unexpected channel operation");
        }
        if (operation instanceof ProgramChangeOperation) {
          programChangeEventCount.incrementAndGet();
          LOG.debug("Program change event : {} : {}", operation, programChangeEventCount.get());
        }
      } else {
        LOG.warn("Unexpected operation");
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    ProjectBuilder builder = new ProjectBuilder(environment.getContainer())
        .addLayer("groove").withLayers("groove")
        .changeProgramTo("violin")
        .withOffset(OFFSET)
        .withLength(LENGTH)
        .addMetronome();
    LOG.debug("*** *** *** Added first metronome");
    environment.start();
    Sequence channel0metronome = builder.getSequenceByOrder(0);
    Channel channel0 = builder.getChannelByOrder(0);
    assertEquals("Unexpected number of program changes", 1,
        environment.getContainer().getChannelStream().count());
    LOG.debug("Program changes : {}", environment.getContainer().getChannels());
    assertEquals("Unexpected program change", "violin", channel0.getProgramName());

    try {
      channel0Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough channel 0 events fired", 0, channel0Events.getCount());
      builder
          .withChannel(1).changeProgramTo("cello")
          .withLength(LENGTH).withOffset(OFFSET + LENGTH * 2).addMetronome()
          .withChannel(2).changeProgramTo("piano");
      LOG.debug("*** *** *** Changed channel 1 to cello, channel 2 to piano and adding channel 1"
          + " metronome");
      environment.getContainer().apply();
      channel1Events.await(1000, TimeUnit.MILLISECONDS);
      /*
       * Verify that only the explicit program changes are received and earlier ones are not
       * repeated.
       */
      assertEquals("Incorrect number of program change events", 3, programChangeEventCount.get());
      assertEquals("Not enough channel 1 events fired", 0, channel1Events.getCount());
      /*
       * Verify that changing back to early change that had since been overwritten is picked up.
       */
      builder
          .withChannel(1).changeProgramTo("violin")
          .withChannel(3).withLength(LENGTH).withOffset(OFFSET + LENGTH * 4).addMetronome()
          .removeSequence(channel0metronome);
      LOG.debug("*** *** *** Removed channel 0 metronome");
      environment.getContainer().apply();
      channel3Events.await(1000, TimeUnit.MILLISECONDS);
      assertEquals("Not enough channel 3 events fired", 0, channel3Events.getCount());
      assertEquals("Incorrect number of program change events", 4,
          programChangeEventCount.get());
    } finally {
      environment.stop();
    }

    OdinSequencerStatistics statistics = environment.getSequencer().getStatistics();
    assertEquals("Number of program changes not correct", 4,
        statistics.getProgramChangeCount());
    assertEquals("Number of added tracks not correct", 3,
        statistics.getTrackStatistics().getAddedCount());
    assertEquals("Number of removed tracks not correct", 1,
        statistics.getTrackStatistics().getRemovedCount());
  }
}
