package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

/**
 * Sequenced Operation Receiver.
 */
public interface OperationReceiver {
  void send(Operation operation, long time) throws OdinException;
}
