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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.purplepip.odin.common.OdinRuntimeException;
import java.io.IOException;

public class PrettyJson {
  /**
   * Pretty print given JSON string.
   *
   * @param json JSON to pretty print
   * @return pretty printed JSON
   */
  public static String toPrettyJson(String json) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    try {
      JsonNode tree = mapper.readTree(json);
      Object sortedTree = mapper.treeToValue(tree, Object.class);
      new JsonNodeSorter().sort(sortedTree);
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedTree);
    } catch (IOException e) {
      throw new OdinRuntimeException("Cannot parse JSON " + json, e);
    }
  }
}
