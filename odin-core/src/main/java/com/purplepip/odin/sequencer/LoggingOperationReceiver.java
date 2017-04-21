package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation receiver that logs each operation.
 */
public class LoggingOperationReceiver implements OperationReceiver {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingOperationReceiver.class);

  @Override
  public void send(Operation operation, long time) throws OdinException {
    LOG.debug("Operation {} at time {}", operation.getNumber(), time);
  }
}
