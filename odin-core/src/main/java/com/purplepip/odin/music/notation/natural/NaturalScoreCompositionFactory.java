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

package com.purplepip.odin.music.notation.natural;

import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.EventsCompositionBuilder;
import com.purplepip.odin.music.notation.NaturalScoreLexer;
import com.purplepip.odin.music.notation.NaturalScoreParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Composition factory.
 */
@Slf4j
public class NaturalScoreCompositionFactory {
  /**
   * Create composition from the given notation.
   *
   * @param notation notation to create composition for
   * @return composition
   */
  public EventsComposition create(String notation) {
    if (notation == null || notation.trim().length() == 0) {
      LOG.debug("Notation is empty, defaulting to an empty measure");
      return new EventsCompositionBuilder().withMinimumMeasures(
          NaturalScoreCompositionListener.MINIMUM_MEASURES).create();
    }
    Lexer lexer = new NaturalScoreLexer(CharStreams.fromString(notation));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    NaturalScoreParser parser = new NaturalScoreParser(tokens);
    NaturalScoreCompositionListener compositionListener = new NaturalScoreCompositionListener();
    new ParseTreeWalker().walk(compositionListener, parser.composition());
    return compositionListener.getComposition();
  }
}
