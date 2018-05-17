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

package com.purplepip.odin.api.services.system;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.devices.UnavailableDevice;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnvironmentSerializer extends StdSerializer<Environment> {
  private static final long serialVersionUID = 1;

  protected EnvironmentSerializer() {
    super(Environment.class);
  }

  @Override
  public void serialize(Environment environment, JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("handles", environment.getHandles());
    jsonGenerator.writeObjectField("devices",
        environment.getHandles().stream().map(handle -> {
          try {
            return handle.connect();
          } catch (DeviceUnavailableException e) {
            LOG.warn("Cannot connect to {} : {}", handle, e.getMessage());
            LOG.debug("Error when connecting to device ", e);
            return new UnavailableDevice(handle);
          }
        }).collect(Collectors.toList()));
    jsonGenerator.writeEndObject();
  }
}
