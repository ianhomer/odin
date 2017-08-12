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

package com.purplepip.odin.server.rest.event;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequencer.Channel;
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
public class ChannelEventHandler {
  @Autowired
  private ProjectContainer projectContainer;

  /**
   * Apply project after channel save.
   *
   * @param channel channel saved
   */
  @HandleAfterSave
  public void handleChannelSave(Channel channel) {
    LOG.info("Channel saved");
    projectContainer.load();
    projectContainer.apply();
  }

  /**
   * Apply project after channel create.
   *
   * @param channel channel created
   */
  @HandleAfterCreate
  public void handleChannelCreate(Channel channel) {
    LOG.info("Channel created");
    projectContainer.load();
    projectContainer.apply();
  }

  /**
   * Apply project after channel delete.
   *
   * @param channel channel deleted
   */
  @HandleAfterDelete
  public void handleChannelDelete(Channel channel) {
    LOG.info("Channel deleted");
    projectContainer.load();
    projectContainer.apply();
  }
}
