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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JsonArrayComparatorTest {
  private Comparator<Object> comparator = new JsonArrayComparator();

  @Test
  public void testCompare() {
    assertEquals(0, comparator.compare(null, null));
    assertEquals(1, comparator.compare(null, "a"));
    assertEquals(-1, comparator.compare("a", null));
    assertEquals(0, comparator.compare("a", "a"));

    assertKeyedValue(0, "name", "a", "a");
    assertKeyedValue(-1, "name", "a", "b");
    assertKeyedValue(1, "name", "b", "a");
    assertKeyedValue(-1, "name", "a", new Object());
    assertKeyedValue(-1, "name", "a", null);
    assertKeyedValue(0, "time", "1", "1");
    assertKeyedValue(-1, "time", "1", "2");
    assertKeyedValue(1, "time", "2", "1");

    assertKeyedValues(0, new String[]{"name", "time"},
        new String[]{"a", "1"}, new String[]{"a", "1"});
    assertKeyedValues(-1, new String[]{"name", "time"},
        new String[]{"a", "1"}, new String[]{"a", "2"});
  }

  private void assertKeyedValue(int expected, String key, Object value1, Object value2) {
    Map<String, Object> map1 = new HashMap<>();
    Map<String, Object> map2 = new HashMap<>();
    map1.put(key, value1);
    map2.put(key, value2);
    assertEquals(expected, comparator.compare(map1, map2));
  }

  private void assertKeyedValues(int expected, String[] keys, Object[] values1, Object[] values2) {
    Map<String, Object> map1 = new HashMap<>();
    Map<String, Object> map2 = new HashMap<>();
    for (int i = 0; i < keys.length; i++) {
      map1.put(keys[i], values1[i]);
      map2.put(keys[i], values2[i]);
    }

    assertEquals(
        expected, comparator.compare(map1, map2), "map 1 " + map1 + " ; map2 = " + map2);
  }

}