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

package com.purplepip.odin.server.services.schema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SequenceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SchemaController {
  private SequenceFactory factory = new SequenceFactory();

  @Autowired
  ObjectMapper objectMapper;

  @RequestMapping("/services/schema/{name}")
  public JsonSchema getSchema(
      @PathVariable(value = "name") String name)
      throws JsonMappingException, OdinException {
    LOG.debug("Requesting schema for {}", name);
    JsonSchemaGenerator  schemaGenerator = new JsonSchemaGenerator(objectMapper);
    Class<? extends Sequence> clazz = factory.getSequenceClass(name);
    if (clazz == null) {
      throw new OdinException("Cannot find registered sequence class " + name);
    }
    return schemaGenerator.generateSchema(factory.getSequenceClass(name));
  }
}
