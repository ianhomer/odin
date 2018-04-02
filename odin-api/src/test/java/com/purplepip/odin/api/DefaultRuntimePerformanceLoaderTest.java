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

package com.purplepip.odin.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.api.rest.repositories.PerformanceRepository;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.store.domain.PersistableSequence;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@ActiveProfiles({"noServices", "noAuditing"})
public class DefaultRuntimePerformanceLoaderTest {
  @Autowired
  private PerformanceContainer performanceContainer;

  @Autowired
  private PerformanceRepository performanceRepository;

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private DefaultRuntimePerformanceLoader loader;

  @Test
  public void testDefaultRuntimePerformanceLoader() {
    PerformanceContainer reloadedContainer =
        new PerformanceContainer(performanceRepository.findAll().iterator().next());
    assertThat(reloadedContainer.getChannels()).isNotEmpty();
    assertThat(reloadedContainer.getChannelStream().count()).isGreaterThan(3);
    assertThat(reloadedContainer.getLayerStream().count()).isGreaterThan(4);
    assertThat(reloadedContainer.getSequenceStream().count()).isGreaterThan(4);
    Optional<SequenceConfiguration> pianoSequence = reloadedContainer.getSequenceStream()
        .filter(s -> s.getName().equals("piano-a")).findFirst();
    assertThat(pianoSequence.isPresent()).isTrue();
    assertThat(pianoSequence.get().getLayers().size()).isEqualTo(2);

    assertThat(reloadedContainer.getLayerStream().count()).isGreaterThan(9);
    assertThat(reloadedContainer.getSequenceStream()
        .filter(sequence -> sequence instanceof PersistableSequence).count()).isGreaterThan(5);
  }
}