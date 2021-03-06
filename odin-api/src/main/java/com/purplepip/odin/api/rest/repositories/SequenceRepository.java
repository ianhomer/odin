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

package com.purplepip.odin.api.rest.repositories;

import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.store.domain.PersistableSequence;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Sequence repository.
 */
@RepositoryRestResource(path = "sequence", collectionResourceRel = "sequence",
    itemResourceRel = "sequence")
@Profile("!noStore")
public interface SequenceRepository extends CrudRepository<PersistableSequence, Long> {
  List<SequenceConfiguration> findByChannel(int channel);

  List<Sequence> findByName(String name);
}
