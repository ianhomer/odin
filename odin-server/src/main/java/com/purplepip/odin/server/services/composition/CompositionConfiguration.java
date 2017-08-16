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

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.music.notation.easy.composition.EasyComposition;
import com.purplepip.odin.music.notation.easy.composition.EasyMeasure;
import com.purplepip.odin.music.notation.easy.composition.EasyStaff;
import com.purplepip.odin.music.notation.easy.composition.EasyVoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class CompositionConfiguration {
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

  /**
   * Customize the JSON output from composition service.
   *
   * @return jackson object mapper customizer
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizeJson() {
    return new Jackson2ObjectMapperBuilderCustomizer() {
      @Override
      public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.serializerByType(EasyComposition.class,  compositionSerializer);
        jacksonObjectMapperBuilder.serializerByType(EasyMeasure.class,  measureSerializer);
        jacksonObjectMapperBuilder.serializerByType(EasyStaff.class, staffSerializer);
        jacksonObjectMapperBuilder.serializerByType(EasyVoice.class, voiceSerializer);
        jacksonObjectMapperBuilder.serializerByType(Rational.class,  rationalSerializer);
      }
    };
  }
}
