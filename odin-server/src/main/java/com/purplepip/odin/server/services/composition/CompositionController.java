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

import com.codahale.metrics.annotation.Timed;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.notation.easy.EasyScoreCompositionBuilder;
import com.purplepip.odin.music.notation.natural.NaturalScoreCompositionFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Profile("!noServices")
public class CompositionController {
  private final NaturalScoreCompositionFactory factory = new NaturalScoreCompositionFactory();

  @Timed
  @RequestMapping("/services/composition")
  public Composition createCompositionFromNotation(
      @RequestParam(value = "notation", defaultValue = "C") String notation) {
    return new EasyScoreCompositionBuilder(factory.create(notation)).build();
  }
}
