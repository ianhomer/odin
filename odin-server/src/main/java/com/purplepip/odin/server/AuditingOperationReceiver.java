/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.server;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.server.rest.domain.PersistableOperation;
import com.purplepip.odin.server.rest.repositories.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditingOperationReceiver implements OperationReceiver {
  @Autowired
  private OperationRepository repository;

  @Override
  public void send(Operation operation, long time) throws OdinException {
    PersistableOperation persistableOperation = new PersistableOperation();
    persistableOperation.setMessage(operation.toString());
    persistableOperation.setTime(time);
    repository.save(persistableOperation);
  }
}
