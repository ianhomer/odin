package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

/**
 * Sequenced NoteOnOperation Receiver.
 */
@FunctionalInterface
public interface OperationReceiver {
  void send(Operation operation, long time) throws OdinException;
}
