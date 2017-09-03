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

package com.purplepip.odin.server.services.composition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.purplepip.odin.music.notation.easy.composition.EasyMeasure;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeasureSerializer extends StdSerializer<EasyMeasure> {
  private static final long serialVersionUID = 1;

  public MeasureSerializer() {
    super(EasyMeasure.class);
  }

  @Override
  public void serialize(EasyMeasure measure,
                        JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("time", measure.getTime().toString());
    jsonGenerator.writeObjectField("key", measure.getKey());
    jsonGenerator.writeObjectField("staves",
        measure.stream().collect(Collectors.toList()));
    jsonGenerator.writeEndObject();
  }
}
