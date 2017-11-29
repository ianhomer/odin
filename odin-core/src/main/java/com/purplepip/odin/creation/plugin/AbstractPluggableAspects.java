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

package com.purplepip.odin.creation.plugin;

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.bag.Thing;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPluggableAspects<A extends PluggableAspect<C>,
    C extends Thing> extends MutableThings<A> {
  /**
   * Refresh the bag of aspects.
   *
   * @param configurationStream configuration stream to use to do the refresh
   * @param aspectCreator creator of new aspects
   */
  public final void refresh(Stream<C> configurationStream,
                      Function<C, A> aspectCreator) {

    Set<Long> ids = getIds();
    configurationStream.forEach(configuration -> {
      ids.remove(configuration.getId());

      /*
       * Add aspect if not present in conductors.
       */
      Optional<A> existing = stream()
          .filter(aspect -> aspect.getId() == configuration.getId()).findFirst();

      A aspect;
      if (existing.isPresent()) {
        aspect = existing.get();
        if (aspect.getConfiguration().equals(configuration)) {
          LOG.debug("Aspect {} already added and unchanged", configuration);
        } else {
          LOG.debug("Updating aspect for {}", configuration);
          incrementUpdatedCount();
          aspect.setConfiguration(configuration);
        }
      } else {
        LOG.debug("Creating new aspect for {}", configuration);
        aspect = aspectCreator.apply(configuration);
        add(aspect);
      }
    });

    /*
     * Remove if not found.
     */
    removeIf(entry -> ids.contains(entry.getKey()) || entry.getValue().isVoid());

    afterRefresh();
  }

  /**
   * Apply changes after refresh.
   */
  protected abstract void afterRefresh();
}
