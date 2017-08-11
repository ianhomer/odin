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

package com.purplepip.odin.music.notation.natural;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.notation.NaturalScoreLexer;
import com.purplepip.odin.music.notation.NaturalScoreParser;
import com.purplepip.odin.music.notation.events.CompositionTestNotation;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class NaturalScoreParserTest {
  private ParseTree getTree(String notation) {
    Lexer lexer = new NaturalScoreLexer(CharStreams.fromString(notation));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    NaturalScoreParser parser = new NaturalScoreParser(tokens);
    return parser.composition();
  }

  @Test
  public void testParser() {
    ParseTree tree = getTree("C#5/q, B4, A4, G#4");
    NaturalScoreTestListener listener = new NaturalScoreTestListener();
    new ParseTreeWalker().walk(listener, tree);
    assertEquals(4, listener.getNoteCount());
  }

  @Test
  public void testBasicCompositions() {
    Map<String, String> notations = new LinkedHashMap<>();

    notations.put("C#5/q, B4, A4, G#4",
        "0.1-61 1.1-59 2.1-57 3.1-56 ");
    notations.put("C5/q, B4, A4, G#4",
        "0.1-60 1.1-59 2.1-57 3.1-56 ");
    notations.put("C5/q, Bb4, A4, G#4",
        "0.1-60 1.1-58 2.1-57 3.1-56 ");
    assertCompositionsOk(notations);
  }

  @Test
  public void testBeatVariations() {
    Map<String, String> notations = new LinkedHashMap<>();
    notations.put("C5/h, C5, C5, C5", "0.2-60 2.2-60 4.2-60 6.2-60 ");
    notations.put("C5/8, C5, C5, C5", "0.½-60 ½.½-60 1.½-60 1½.½-60 ");
    notations.put("C5/8, C5, C5/q, C5", "0.½-60 ½.½-60 1.1-60 2.1-60 ");
    assertCompositionsOk(notations);
  }

  @Test
  public void testIncompleteMeasures() {
    Map<String, String> notations = new LinkedHashMap<>();
    notations.put("C5/q, C, C/8", "0.1-60 1.1-60 2.½-60 ");
    assertCompositionsOk(notations);
  }

  private void assertCompositionsOk(Map<String, String> notations) {
    for (Map.Entry<String, String> entry : notations.entrySet()) {
      ParseTree tree = getTree(entry.getKey());

      LOG.debug("Testing composition");
      NaturalScoreCompositionListener compositionListener = new NaturalScoreCompositionListener();
      new ParseTreeWalker().walk(compositionListener, tree);
      EventsComposition composition = compositionListener.getComposition();
      Assert.assertEquals(entry.getValue(), new CompositionTestNotation(composition).getBody());
    }
  }
}
