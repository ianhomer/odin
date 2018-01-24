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
import java.util.Map;

/**
 * Compare elements in a Json array by comparing the name property in the element if the element
 * is a Map.  This allows arrays of maps to be sorted.
 */
public class JsonArrayComparator implements Comparator<Object> {
  @Override
  public int compare(Object o1, Object o2) {
    if (o1 instanceof Map && o2 instanceof Map) {
      Object name1 = ((Map) o1).get("name");
      Object name2 = ((Map) o2).get("name");
      if (name1 == null || name2 == null) {
        return 0;
      } else if (name1 instanceof String && name2 instanceof String) {
        return ((String) name1).compareTo((String) name2);
      } else {
        return 0;
      }
    } else {
      return o1.hashCode() - o2.hashCode();
    }
  }
}
