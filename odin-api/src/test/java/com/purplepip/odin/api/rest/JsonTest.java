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

package com.purplepip.odin.api.rest;

import static com.purplepip.odin.api.rest.Json.withJsonModule;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class JsonTest {
  @Test
  void testJson() {
    String json = new Json(withJsonModule(new ObjectMapper()))
        .property("name", "new-notations-name")
        .property("performance", "/api/rest/performance/1")
        .property("type", "Notation")
        .properties()
        .property("notation", "A B C D")
        .property("format", "natural")
        .toString();

    assertEquals("{"
        + "\"performance\":\"/api/rest/performance/1\""
        + ",\"name\":\"new-notations-name\""
        + ",\"type\":\"Notation\""
        + ",\"properties\":{\"notation\":\"A B C D\",\"format\""
        + ":\"natural\"}}", json);
  }
}