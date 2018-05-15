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

package com.purplepip.odin.api;

import com.purplepip.odin.api.math.RationalDeserializer;
import com.purplepip.odin.api.math.RationalSerializer;
import com.purplepip.odin.api.rest.Json;
import com.purplepip.odin.api.rest.JsonSerializer;
import com.purplepip.odin.api.services.composition.CompositionSerializer;
import com.purplepip.odin.api.services.composition.MeasureSerializer;
import com.purplepip.odin.api.services.composition.StaffSerializer;
import com.purplepip.odin.api.services.composition.VoiceSerializer;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.music.notation.easy.composition.EasyComposition;
import com.purplepip.odin.music.notation.easy.composition.EasyMeasure;
import com.purplepip.odin.music.notation.easy.composition.EasyStaff;
import com.purplepip.odin.music.notation.easy.composition.EasyVoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JSON configuration for the system.  Note that this configuration is also used by the REST
 * service where given class types are used.
 */
@Configuration
public class JsonConfiguration {
  @Autowired
  private CompositionSerializer compositionSerializer;

  @Autowired
  private MeasureSerializer measureSerializer;

  @Autowired
  private StaffSerializer staffSerializer;

  @Autowired
  private VoiceSerializer voiceSerializer;

  @Autowired
  private RationalSerializer rationalSerializer;

  @Autowired
  private RationalDeserializer rationalDeserializer;

  @Autowired
  private JsonSerializer jsonSerializer;

  /**
   * Customize the JSON output from composition service.
   *
   * @return jackson object mapper customizer
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizeJson() {
    return jacksonObjectMapperBuilder -> {
      jacksonObjectMapperBuilder.serializerByType(EasyComposition.class, compositionSerializer);
      jacksonObjectMapperBuilder.serializerByType(EasyMeasure.class, measureSerializer);
      jacksonObjectMapperBuilder.serializerByType(EasyStaff.class, staffSerializer);
      jacksonObjectMapperBuilder.serializerByType(EasyVoice.class, voiceSerializer);
      jacksonObjectMapperBuilder.serializerByType(Json.class, jsonSerializer);
      jacksonObjectMapperBuilder.serializerByType(Rational.class, rationalSerializer);
      jacksonObjectMapperBuilder.deserializerByType(Rational.class, rationalDeserializer);
    };
  }
}
