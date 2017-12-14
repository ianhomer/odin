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

package com.purplepip.odin.server.services.composition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.purplepip.odin.math.Rational;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
/*
 * TODO : Move this to generic package since this is to be used by whole REST service.
 */
@Slf4j
public class RationalSerializer extends StdSerializer<Rational> {
  private static final long serialVersionUID = 1;

  public RationalSerializer() {
    super(Rational.class);
  }

  @Override
  public void serialize(Rational rational,
                        JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {
    LOG.debug("Serializing : {}", rational);
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("numerator", rational.getNumerator());
    jsonGenerator.writeObjectField("denominator", rational.getDenominator());
    jsonGenerator.writeEndObject();
  }
}
