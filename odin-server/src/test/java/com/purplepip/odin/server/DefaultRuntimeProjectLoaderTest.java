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

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class DefaultRuntimeProjectLoaderTest {
  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectContainer projectContainer;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testDefaultRuntimeProjectLoader() throws Exception {
    assertThat(projectContainer.getChannels()).isEmpty();
    DefaultRuntimeProjectLoader loader =
        new DefaultRuntimeProjectLoader(projectRepository, projectContainer);
    loader.run(null);
    assertThat(projectContainer.getChannels()).isNotEmpty();
  }
}