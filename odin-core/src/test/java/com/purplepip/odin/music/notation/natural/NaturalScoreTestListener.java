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

import com.purplepip.odin.music.notation.NaturalScoreBaseListener;
import com.purplepip.odin.music.notation.NaturalScoreParser;

public class NaturalScoreTestListener extends NaturalScoreBaseListener {
  private int noteCount;

  public int getNoteCount() {
    return noteCount;
  }

  @Override public void enterNote(NaturalScoreParser.NoteContext ctx) {
    noteCount++;
  }
}
