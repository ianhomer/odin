package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;

/**
 * Sequencer factory used in tests.
 */
class TestSequencerFactory {
  /**
   * Create default sequencer used in tests.
   *
   * @param operationReceiver operation receiver to use
   * @return Odin sequencer
   * @throws OdinException exception
   */
  OdinSequencer createDefaultSequencer(OperationReceiver operationReceiver)
      throws OdinException {
    return new OdinSequencer(
        new DefaultOdinSequencerConfiguration()
            .setMeasureProvider(new StaticMeasureProvider(4))
            .setBeatsPerMinute(new StaticBeatsPerMinute(1000))
            .setOperationReceiver(operationReceiver)
            .setMicrosecondPositionProvider(new DefaultMicrosecondPositionProvider()));
  }
}
