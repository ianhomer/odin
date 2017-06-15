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
import com.purplepip.odin.project.ProjectLoadListener;
import com.purplepip.odin.project.ProjectSaveListener;
import com.purplepip.odin.server.rest.domain.PersistableProject;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Project container that contains a persistable project.
 */
@Component
@Slf4j
public class PersistableProjectListener implements
    ProjectSaveListener, ProjectLoadListener, InitializingBean {

  @Autowired
  private ProjectContainer projectContainer;

  @Autowired
  private ProjectRepository projectRepository;

  /**
   * Refresh container.
   */
  @Transactional
  @Override
  public void onProjectLoad(ProjectContainer container) {
    LOG.info("Reloading project");
    container.setProject(projectRepository.findByName(container.getName()));
  }

  @Override
  public void onProjectSave(Project project) {
    if (project instanceof PersistableProject) {
      LOG.info("Saving project");
      projectRepository.save((PersistableProject) project);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    projectContainer.addSaveListener(this);
    projectContainer.addLoadListener(this);
  }
}
