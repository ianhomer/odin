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

package com.purplepip.odin.server.rest.repositories;

import com.purplepip.odin.store.domain.PersistableChannel;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Channel repository.
 */
@RepositoryRestResource(path = "channel",
    collectionResourceRel = "channel", collectionResourceDescription = @Description("channel"),
    itemResourceRel = "channel", itemResourceDescription = @Description("channel"))
@Profile("!noStore")
public interface ChannelRepository extends CrudRepository<PersistableChannel, Long> {
}
