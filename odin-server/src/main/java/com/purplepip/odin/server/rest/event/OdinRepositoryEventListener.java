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

package com.purplepip.odin.server.rest.event;

import static com.purplepip.odin.store.Persistables.toThingString;

import com.purplepip.odin.performance.PerformanceContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!noStore")
@Slf4j
public class OdinRepositoryEventListener extends AbstractRepositoryEventListener {
  @Autowired
  private PerformanceContainer performanceContainer;

  @Override
  public void onBeforeSave(Object entity) {
    LOG.info("onBeforeSave {}", entity);
  }

  @Override
  public void onAfterSave(Object entity) {
    LOG.info("onAfterSave {}", entity);
  }

  @Override
  public void onAfterCreate(Object entity) {
    LOG.info("onAfterCreate {}", entity);
  }

  @Override
  public void onBeforeDelete(Object entity) {
    if (LOG.isInfoEnabled()) {
      LOG.info("onBeforeDelete {}", toThingString(entity));
    }
  }

  @Override
  public void onAfterDelete(Object entity) {
    if (LOG.isInfoEnabled()) {
      LOG.info("onAfterDelete {}", toThingString(entity));
    }
  }
}
