package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Operation receiver that captures the operations.
 */
public class CapturingOperationReceiver implements OperationHandler {
  private final List<OperationEvent> operationEventList = new ArrayList<>();

  @Override
  public void handle(Operation operation, long time) throws OdinException {
    operationEventList.add(new OperationEvent(operation, time));
  }

  public List<OperationEvent> getList() {
    return operationEventList;
  }
}
