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

import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.server.rest.domain.PersistableLayer;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Channel repository.
 */
@RepositoryRestResource(path = "layers",
    collectionResourceRel = "layers", collectionResourceDescription = @Description("layers"),
    itemResourceRel = "layer", itemResourceDescription = @Description("layer"))
public interface LayerRepository extends PagingAndSortingRepository<PersistableLayer, Long> {
  List<Layer> findByName(String name);
}
