package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;

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
    OdinSequencer sequencer = new OdinSequencer(
        new OdinSequencerConfiguration()
            .setBeatsPerMinute(new StaticBeatsPerMinute(1000))
            .setOperationReceiver(operationReceiver)
            .setMicrosecondPositionProvider(new DefaultMicrosecondPositionProvider()));

    new SequenceBuilder(sequencer, new StaticMeasureProvider(4))
        .addMetronome();
    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 8) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        LOG.error("Sleep interrupted", e);
      }
    }

    sequencer.stop();
    LOG.info("... stopped");

    assertEquals("Number of operations sent not correct", 16, operationReceiver.getList().size());
  }
}
