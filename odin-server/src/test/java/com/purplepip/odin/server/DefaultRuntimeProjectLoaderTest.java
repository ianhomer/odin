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

import static com.purplepip.odin.server.DefaultProjectCreator.DEFAULT_PROJECT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import com.purplepip.odin.store.domain.PersistablePattern;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"noAuditing"})
public class DefaultRuntimeProjectLoaderTest {
  @Autowired
  private ProjectContainer projectContainer;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private DefaultRuntimeProjectLoader loader;

  @Test
  public void testDefaultRuntimeProjectLoader() throws Exception {
    ProjectContainer reloadedContainer =
        new ProjectContainer(projectRepository.findByName(DEFAULT_PROJECT_NAME));
    assertThat(reloadedContainer.getChannels()).isNotEmpty();
    assertThat(reloadedContainer.getChannelStream().count()).isGreaterThan(3);
    assertThat(reloadedContainer.getLayerStream().count()).isGreaterThan(4);
    assertThat(reloadedContainer.getSequenceStream().count()).isGreaterThan(4);
    Optional<Sequence> pianoSequence = reloadedContainer.getSequenceStream()
        .filter(s -> s.getName().equals("piano-a")).findFirst();
    assertThat(pianoSequence.isPresent()).isTrue();
    assertThat(pianoSequence.get().getLayers().size()).isEqualTo(2);

    assertThat(reloadedContainer.getLayerStream().count()).isGreaterThan(9);
    assertThat(reloadedContainer.getSequenceStream()
        .filter(sequence -> sequence instanceof PersistablePattern).count()).isGreaterThan(5);
  }
}