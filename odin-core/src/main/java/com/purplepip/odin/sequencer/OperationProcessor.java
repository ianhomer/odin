package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

/**
 * NoteOnOperation Processor that is responsible for firing the operations when the time is right.
 */
public interface OperationProcessor {
  void send(Operation operation, long time) throws OdinException;

  void close();
}
