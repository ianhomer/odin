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

package com.purplepip.odin.store.domain;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class PersistableHelper {
  private PersistableHelper() {
  }

  /*
   * TODO : Can we find a better of way of making sure duplicates do not appear in this list.
   * I believe we need a list because a) JSON patching wants list not set and b) logically
   * order of layers will useful domain logic to based order that layers are applied.
   */
  static void removeDuplicates(List<String> list) {
    Set<String> duplicateLayers =
        list.stream().filter(i -> Collections.frequency(list, i) > 1)
            .collect(Collectors.toSet());
    if (!duplicateLayers.isEmpty()) {
      LOG.warn("Removing duplicates layers {}", duplicateLayers);
      Set<String> layers = Sets.newHashSet(list);
      list.clear();
      list.addAll(layers);
    }
  }
}
