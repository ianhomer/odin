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

import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Pattern repository.
 */
@RepositoryRestResource(path = "patterns", collectionResourceRel = "patterns",
    itemResourceRel = "pattern")
public interface PatternRepository extends CrudRepository<PersistablePattern, Long> {
  List<Pattern> findByChannel(int channel);
}
