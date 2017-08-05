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

package com.purplepip.odin.music.notation;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.composition.Composition;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

@Slf4j
public class EasyFlowParserTest {
  @Test
  public void testParser() {
    EasyFlowLexer lexer = new EasyFlowLexer(CharStreams.fromString("C#5/q, B4, A4, G#4"));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    EasyFlowParser parser = new EasyFlowParser(tokens);
    ParseTree tree = parser.composition();
    ParseTreeWalker walker = new ParseTreeWalker();
    TestEasyFlowListener listener = new TestEasyFlowListener();
    walker.walk(listener, tree);
    assertEquals(4 , listener.getNoteCount());

    LOG.debug("Testing composition");
    EasyFlowCompositionListener compositionListener = new EasyFlowCompositionListener();
    walker.walk(compositionListener, tree);
    Composition composition = compositionListener.getComposition();
    assertEquals(4, composition.size());
    assertEquals("0.1-61 1.1-59 2.1-57 3.1-56 ",
        new CompositionNotation(composition).getBody());
  }
}
