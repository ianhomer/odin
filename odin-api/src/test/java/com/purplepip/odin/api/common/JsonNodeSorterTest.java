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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;

class JsonNodeSorterTest {
  @Test
  void testSort() {
    Map<String, Object> node = new LinkedHashMap<>();
    node.put("element1", "string-node");
    List<Map> list = new ArrayList<>();
    list.add(Maps.newHashMap("name", "b"));
    list.add(Maps.newHashMap("name", "c"));
    list.add(Maps.newHashMap("name", "a"));
    node.put("element1", "string-node");
    node.put("element2", list);
    new JsonNodeSorter().sort(node);
    assertEquals("string-node", node.values().iterator().next());
    assertEquals("a", list.iterator().next().get("name"));
  }
}