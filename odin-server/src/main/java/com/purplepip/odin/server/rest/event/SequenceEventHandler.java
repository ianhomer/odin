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

import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.performance.PerformanceContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
@Profile("!noStore")
@Slf4j
public class SequenceEventHandler {
  @Autowired
  private PerformanceContainer performanceContainer;

  /**
   * Apply performance after sequence save.
   *
   * @param sequence sequence saved
   */
  @HandleAfterSave
  public void handleSequenceSave(SequenceConfiguration sequence) {
    LOG.info("Sequence saved : {}", sequence.getName());
    performanceContainer.load();
    performanceContainer.apply();
  }

  /**
   * Apply performance after sequence create.
   *
   * @param sequence sequence created
   */
  @HandleAfterCreate
  public void handleSequenceCreate(SequenceConfiguration sequence) {
    LOG.info("Sequence created : {}", sequence.getName());
    performanceContainer.load();
    performanceContainer.apply();
  }

  /**
   * Apply performance after sequence delete.
   *
   * @param sequence sequence deleted
   */
  @HandleAfterDelete
  public void handleSequenceDelete(SequenceConfiguration sequence) {
    LOG.info("Sequence deleted : {}", sequence.getName());
    performanceContainer.load();
    performanceContainer.apply();
  }
}
