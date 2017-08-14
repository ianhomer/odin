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

import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import com.purplepip.odin.store.domain.PersistableProject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!noStore")
@Order(1)
@Slf4j
public class DefaultProjectCreater implements ApplicationRunner {
  public static final String DEFAULT_PROJECT_NAME = "defaultProject";

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectContainer projectContainer;

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {
    Project project = projectRepository.findByName(DEFAULT_PROJECT_NAME);
    if (project == null) {
      project = new PersistableProject();
      ((PersistableProject) project).setName(DEFAULT_PROJECT_NAME);
      projectRepository.save((PersistableProject) project);
      LOG.info("Created default project");
    }
    projectContainer.setProject(project);
  }
}
