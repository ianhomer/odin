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

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.server.rest.PersistableProjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Order(2)
@Slf4j
public class DefaultRuntimeProjectLoader implements ApplicationRunner {
  private ProjectContainer projectContainer;

  @Autowired
  public DefaultRuntimeProjectLoader(ProjectContainer projectContainer) {
    this.projectContainer = projectContainer;
  }

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {
    if (projectContainer.isEmpty()) {
      new PersistableProjectBuilder(projectContainer)
          .addMetronome()
          .withChannel(1).changeProgramTo("bird")
          .withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
          .withChannel(2).changeProgramTo("aahs")
          .withVelocity(20).withNote(42).addPattern(Ticks.BEAT, 15)
          .withChannel(9).changeProgramTo("TR-909")
          .withVelocity(100).withNote(62).addPattern(Ticks.BEAT, 2)
          .withVelocity(40).addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);
      projectContainer.save();
      LOG.info("Default project populated");
    } else {
      LOG.warn("Default project has already been loaded");
    }
  }
}
