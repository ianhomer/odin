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

package com.purplepip.odin.server.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purplepip.odin.common.OdinRuntimeException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON utilities for test cases to simplify boiler plate code.
 */
@Slf4j
public class Json {
  private Map<String, String> object = new HashMap<>();
  private ObjectMapper objectMapper;

  public Json(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Json put(String name, String value) {
    object.put(name, value);
    return this;
  }

  @Override
  public String toString() {
    String string;
    try {
      string = objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new OdinRuntimeException("Cannot write object to string : " + object, e);
    }
    LOG.debug("JSON = {}", string);
    return string;
  }
}
