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

package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.server.PersistableProjectContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
@Slf4j
public class ChannelEventHandler {
  @Autowired
  private PersistableProjectContainer projectContainer;

  @HandleAfterSave
  public void handleChannelSave(Channel channel) {
    LOG.info("Channel saved");
    projectContainer.refresh();
  }

  @HandleAfterCreate
  public void handleChannelCreate(Channel channel) {
    LOG.info("Channel created");
    projectContainer.refresh();
  }

  @HandleAfterDelete
  public void handleChannel(Channel channel) {
    LOG.info("Channel deleted");
    projectContainer.refresh();
  }
}
