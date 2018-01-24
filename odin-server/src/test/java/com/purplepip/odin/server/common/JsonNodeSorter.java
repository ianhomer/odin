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

package com.purplepip.odin.server.common;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Jackson ObjectMapper doesn't seem to have a way to sort arrays of maps in a JSON node.
 * This sorter sorts lists of maps on the entry in the map with key called "name".  This
 * provides a way for snapshots of JSON to be rendered in a consistent manner.  Note that
 * ObjectMapper does sort object properties so we do NOT repeat that logic here.
 */
public class JsonNodeSorter {
  private static final Comparator COMPARATOR = new JsonArrayComparator();

  /**
   * Sort the JSON Node.
   *
   * @param node json node
   */
  public void sort(Object node) {
    if (node instanceof List) {
      sort((List) node);
    } else if (node instanceof Map) {
      sort((Map) node);
    }
  }

  private void sort(Map map) {
    map.values().forEach(this::sort);
  }

  private void sort(List list) {
    list.sort(COMPARATOR);
  }
}
