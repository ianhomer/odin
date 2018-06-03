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

package com.purplepip.odin.devices;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle comparator.  Note that this only compares on names (for now).
 */
public class HandleComparator implements Comparator<Handle> {
  private final List<String> priorityHandleNames;

  HandleComparator(List<Handle> priorityHandles) {
    priorityHandleNames = priorityHandles.stream().map(handle -> handle.getName().toLowerCase())
        .collect(Collectors.toList());
  }

  @Override
  public int compare(Handle o1, Handle o2) {
    /*
     * Compare based on position in priority handle list.
     */
    for (String priorityHandleName : priorityHandleNames) {
      if (o1.getName().toLowerCase().startsWith(priorityHandleName)) {
        if (o2.getName().toLowerCase().startsWith(priorityHandleName)) {
          return o1.getName().compareTo(o2.getName());
        }
        return -1;
      } else if (o2.getName().toLowerCase().contains(priorityHandleName)) {
        return 1;
      }
    }
    /*
     * Otherwise, priority based on string compare.
     */
    return o1.getName().compareTo(o2.getName());
  }
}
