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
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Default operation transmitter that operations.
 */
@Slf4j
public class DefaultOperationTransmitter implements OperationTransmitter {
  private List<OperationReceiver> receivers = new ArrayList<>();

  @Override
  public void handle(Operation operation, long time) throws OdinException {
    receivers.forEach(receiver -> {
      try {
        /*
         * TODO : We currently transmit operation with NOW time, not the original time.  This is
         * from the assumption that we want the incoming signal to always be processed and ASAP.
         * There might be situations where signals are coming in from the future, however to
         * support this we need to make sure the times are synchronized between source and
          * destination devices.
         */
        receiver.handle(operation, -1);
      } catch (OdinException e) {
        LOG.error("Cannot handle operation " + operation + " at time " + time, e);
      }
    });
  }

  @Override
  public void addListener(OperationReceiver receiver) {
    receivers.add(receiver);
  }

  @Override
  public void removeListener(OperationReceiver receiver) {
    receivers.remove(receiver);
  }
}
