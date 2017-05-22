package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * List of operation processors.
 */
public class OperationReceiverCollection implements
    Iterable<OperationReceiver>, OperationReceiver {
  private List<OperationReceiver> operationReceiverList;

  public OperationReceiverCollection(OperationReceiver... operationReceivers) {
    operationReceiverList = Arrays.asList(operationReceivers);
  }

  public OperationReceiverCollection(List<OperationReceiver> operationReceivers) {
    operationReceiverList = new ArrayList<>(operationReceivers);
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    for (OperationReceiver operationReceiver : operationReceiverList) {
      operationReceiver.send(operation, time);
    }
  }

  @Override
  public Iterator<OperationReceiver> iterator() {
    return operationReceiverList.iterator();
  }
}
