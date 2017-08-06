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

package com.purplepip.odin.music.composition;

import com.purplepip.odin.music.notation.EasyFlowCompositionListener;
import com.purplepip.odin.music.notation.EasyFlowLexer;
import com.purplepip.odin.music.notation.EasyFlowParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Composition factory.
 */
public class CompositionFactory {
  /**
   * Create composition from the given notation.
   *
   * @param notation notation to create composition for
   * @return composition
   */
  public Composition create(String notation) {
    EasyFlowLexer lexer = new EasyFlowLexer(CharStreams.fromString(notation));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    EasyFlowParser parser = new EasyFlowParser(tokens);
    EasyFlowCompositionListener compositionListener = new EasyFlowCompositionListener();
    new ParseTreeWalker().walk(compositionListener, parser.composition());
    return compositionListener.getComposition();
  }
}
