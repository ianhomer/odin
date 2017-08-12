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
import com.purplepip.odin.music.operations.AbstractNoteVelocityOperation;
import com.purplepip.odin.sequencer.ChannelOperation;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.server.rest.repositories.OperationRepository;
import com.purplepip.odin.store.domain.PersistableOperation;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditingOperationReceiver implements OperationReceiver, InitializingBean {
  private static final long CLEAN_PERIOD = 30;

  @Autowired
  private OperationRepository repository;

  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

  @Override
  public void send(Operation operation, long time) throws OdinException {
    PersistableOperation persistableOperation = new PersistableOperation();
    persistableOperation.setMessage(operation.toString());
    persistableOperation.setTime(time);
    if (operation instanceof ChannelOperation) {
      persistableOperation.setChannel(((ChannelOperation) operation).getChannel());
      if (operation instanceof AbstractNoteVelocityOperation) {
        AbstractNoteVelocityOperation noteVelocityOperation = (AbstractNoteVelocityOperation)
            operation;
        persistableOperation.setNumber(noteVelocityOperation.getNumber());
        persistableOperation.setVelocity(noteVelocityOperation.getVelocity());
      }
    }
    repository.save(persistableOperation);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    scheduledPool.scheduleAtFixedRate(() -> {
      try {
        long countBefore = repository.count();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -60);
        repository.deleteByCreatedDateBefore(calendar.getTime());
        long countAfter = repository.count();
        LOG.info("Cleaning audit table : reduced from {} to {}", countBefore, countAfter);
      } catch (RuntimeException e) {
        LOG.error("Error whilst executing sequence processing", e);
      }
    },0, CLEAN_PERIOD, TimeUnit.SECONDS);
  }
}
