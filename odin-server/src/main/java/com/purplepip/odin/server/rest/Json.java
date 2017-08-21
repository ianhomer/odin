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
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.purplepip.odin.common.OdinRuntimeException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON utilities for test cases to simplify boiler plate code.
 */
@Slf4j
public class Json {
  private Map<String, Object> object = new HashMap<>();
  private ObjectMapper objectMapper;
  private Deque<Json> stack = new ArrayDeque<>();

  /**
   * Enrich an object mapper with a module to serialise these Json objects.
   *
   * @param mapper mapper to enrich
   * @return the enriched mapper
   */
  public static ObjectMapper withJsonModule(ObjectMapper mapper) {
    SimpleModule module = new SimpleModule();
    module.addSerializer(Json.class, new JsonSerializer());
    mapper.registerModule(module);
    return mapper;
  }

  public Json(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    stack.push(this);
  }

  public Json property(String name, Object value) {
    stack.peek().put(name, value);
    return this;
  }

  public Object getProperty(String name) {
    return object.get(name);
  }

  public Json properties() {
    return  start("properties");
  }

  /**
   * Start object.
   *
   * @param name name of object to start
   * @return this
   */
  public Json start(String name) {
    Json child = new Json(objectMapper);
    put(name, child);
    stack.push(child);
    return this;
  }

  /**
   * End object.
   *
   * @return this
   */
  public Json end() {
    stack.pop();
    return this;
  }

  private void put(String name, Object value) {
    object.put(name, value);
  }

  public Stream<String> getPropertyNames() {
    return object.keySet().stream();
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
