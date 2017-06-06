package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.Ticks;
import org.junit.Test;

/**
 * Complex sequencer test.  This is in a separate class to OdinSequencerTest to make
 * troubleshooting on failure easier.
 */
public class OdinSequencerComplexTest {
  @Test
  public void testComplexSequencer() throws OdinException {
    CapturingOperationReceiver operationReceiver = new CapturingOperationReceiver();
    OdinSequencer sequencer = new TestSequencerFactory().createDefaultSequencer(operationReceiver);
    new SequenceBuilder(sequencer.getProject())
        .addMetronome()
        .withChannel(1).withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
        .withChannel(2).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withChannel(8).withVelocity(100).withNote(42).addPattern(Ticks.BEAT, 15)
        .withChannel(9).withVelocity(70).withNote(62).addPattern(Ticks.BEAT, 2)
        .withVelocity(20)
        .addPattern(Ticks.EIGHTH, 127)
        .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 8) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    sequencer.stop();

    assertTrue("Number of operations sent not correct",
        operationReceiver.getList().size() > 50);
  }
}
