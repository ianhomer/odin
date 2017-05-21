package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

import java.util.Arrays;
import java.util.List;

/**
 * List of operation processors.
 */
public class OperationReceiverCollection implements OperationReceiver {
  private List<OperationReceiver> operationReceiverList;

  public OperationReceiverCollection(OperationReceiver... operationReceivers) {
    operationReceiverList = Arrays.asList(operationReceivers);
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    for (OperationReceiver operationReceiver : operationReceiverList) {
      operationReceiver.send(operation, time);
    }
  }
}
