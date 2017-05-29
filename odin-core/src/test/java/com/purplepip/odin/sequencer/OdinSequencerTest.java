package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.DefaultTick;
import com.purplepip.odin.sequence.Tick;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test odin sequencer.
 */
public class OdinSequencerTest {
  private static final Logger LOG = LoggerFactory.getLogger(OdinSequencerTest.class);

  @Test
  public void testSequencer() throws OdinException {
    CapturingOperationReceiver operationReceiver = new CapturingOperationReceiver();
    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new SequenceBuilder(sequencer.getProject())
        .addMetronome();
    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 8) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    sequencer.stop();
    LOG.info("... stopped");

    assertEquals("Number of operations sent not correct", 16,
        operationReceiver.getList().size());
  }

  @Test
  public void testComplexSequencer() throws OdinException {
    CapturingOperationReceiver operationReceiver = new CapturingOperationReceiver();
    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new SequenceBuilder(sequencer.getProject())
        .addMetronome()
        .withChannel(1).withVelocity(10).withNote(62).addPattern(DefaultTick.BEAT, 4)
        .withChannel(2).withVelocity(70).withNote(62).addPattern(DefaultTick.BEAT, 2)
        .withChannel(8).withVelocity(100).withNote(42).addPattern(DefaultTick.BEAT, 15)
        .withChannel(9).withVelocity(70).withNote(62).addPattern(DefaultTick.BEAT, 2)
        .withVelocity(20)
        .addPattern(DefaultTick.EIGHTH, 127)
        .withNote(46).addPattern(DefaultTick.TWO_THIRDS, 7);

    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 8) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    sequencer.stop();
    LOG.info("... stopped");

    assertTrue("Number of operations sent not correct",
        operationReceiver.getList().size() > 50);
  }
}
