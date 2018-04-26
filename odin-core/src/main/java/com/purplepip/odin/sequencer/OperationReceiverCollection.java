/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.operation.OperationReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * List of operation processors.
 */
public class OperationReceiverCollection implements
    Iterable<OperationReceiver>, OperationReceiver {
  private final List<OperationReceiver> operationReceiverList;

  public OperationReceiverCollection(OperationReceiver... operationReceivers) {
    operationReceiverList = Arrays.asList(operationReceivers);
  }

  public OperationReceiverCollection(List<OperationReceiver> operationReceivers) {
    operationReceiverList = new ArrayList<>(operationReceivers);
  }

  @Override
  public void handle(Operation operation, long time) throws OdinException {
    for (OperationReceiver operationReceiver : operationReceiverList) {
      operationReceiver.handle(operation, time);
    }
  }

  @Override
  public Iterator<OperationReceiver> iterator() {
    return operationReceiverList.iterator();
  }

  @Override
  public void onPerformanceStart() {
    for (OperationReceiver operationReceiver : operationReceiverList) {
      operationReceiver.onPerformanceStart();
    }
  }

  @Override
  public void onPerformanceStop() {
    for (OperationReceiver operationReceiver : operationReceiverList) {
      operationReceiver.onPerformanceStop();
    }
  }
}
