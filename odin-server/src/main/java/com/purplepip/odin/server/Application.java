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

package com.purplepip.odin.server;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.music.notation.easy.composition.EasyComposition;
import com.purplepip.odin.music.notation.easy.composition.EasyMeasure;
import com.purplepip.odin.music.notation.easy.composition.EasyStaff;
import com.purplepip.odin.music.notation.easy.composition.EasyVoice;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.server.services.CompositionSerializer;
import com.purplepip.odin.server.services.MeasureSerializer;
import com.purplepip.odin.server.services.RationalSerializer;
import com.purplepip.odin.server.services.StaffSerializer;
import com.purplepip.odin.server.services.VoiceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Odin Application.
 */
@SpringBootApplication
@ComponentScan({"com.purplepip.odin.server"})
public class Application {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  @Autowired
  private ProjectContainer container;

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
   * Odin server start up.
   *
   * @param args arguments
   */
  public static void main(String[] args) {
    SpringApplicationBuilder builder = new SpringApplicationBuilder()
        .parent(Application.class);
    builder.run(args);
  }

  /**
   * Commands to execute on start up.
   *
   * @param ctx spring application context
   * @return command line runner
   */
  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      sequencer.start();
      container.apply();

      LOG.info("Odin Started.");
    };
  }

  /**
   * Customize the JSON output from this application.
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

