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

package com.purplepip.odin.api.common;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;

/**
 * Compare elements in a Json array by comparing the name property in the element if the element
 * is a Map.  This allows arrays of maps to be sorted.
 */
public class JsonArrayComparator implements Comparator<Object> {
  /*
   * We sort on the first one of these keys that is found in either of the objects we're comparing
   */
  private static final List<String> KEYS =
      Lists.newArrayList("name", "time", "channel", "dateCreated");

  @Override
  public int compare(Object o1, Object o2) {
    if (o1 instanceof Map && o2 instanceof Map) {
      Map map1 = ((Map) o1);
      Map map2 = ((Map) o2);
      for (String key : KEYS) {
        int result = compare(getValues(map1, map2, key));
        if (result != 0) {
          return result;
        }
      }
      /*
       * All compared values are the same.
       */
      return 0;
    } else if (o1 == null && o2 == null) {
      return 0;
    } else if (o2 == null) {
      return -1;
    } else if (o1 == null) {
      return 1;
    } else {
      return o1.hashCode() - o2.hashCode();
    }
  }

  private int compare(Object[] values) {
    if (values[0] == null && values[1] == null) {
      /*
       * Both null => both the same
       */
      return 0;
    } else if (values[1] == null) {
      /*
       * Value 1 null => object 1 should be first
       */
      return -1;
    } else if (values[0] instanceof String && values[1] instanceof String) {
      return ((String) values[0]).compareTo((String) values[1]);
    } else if (values[0] instanceof String) {
      return -1;
    } else if (values[1] instanceof String) {
      return 1;
    } else {
      return values[0].hashCode() - values[1].hashCode();
    }
  }

  private Object[] getValues(Map map1, Map map2, String key) {
    return new Object[]{
        map1.get(key),
        map2.get(key)
    };
  }
}
