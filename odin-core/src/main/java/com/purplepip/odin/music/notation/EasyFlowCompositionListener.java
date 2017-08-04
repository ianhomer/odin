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

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.sequence.DefaultEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EasyFlowCompositionListener extends EasyFlowBaseListener {
  private static final int DEFAULT_VELOCITY = 100;
  private Composition composition = new Composition();

  private NoteLetter noteLetter;
  private int relativeNoteNumber;
  private int octave;
  private int tock;
  private int velocity = DEFAULT_VELOCITY;

  public Composition getComposition() {
    return composition;
  }

  @Override
  public void enterLetter(EasyFlowParser.LetterContext ctx) {
    LOG.debug("Entering note {}", ctx.getText());
    noteLetter = NoteLetter.valueOf(ctx.getText());
    relativeNoteNumber = noteLetter.getValue();
  }

  @Override
  public void enterOctave(EasyFlowParser.OctaveContext ctx) {
    octave = Integer.parseInt(ctx.getText());
  }

  @Override
  public void enterSharp(EasyFlowParser.SharpContext ctx) {
    relativeNoteNumber++;
  }

  @Override
  public void exitNote(EasyFlowParser.NoteContext ctx) {
    composition.addEvent(new DefaultEvent<>(
        new DefaultNote((octave + 1) * 12 + relativeNoteNumber, velocity, 1),
        tock));
    tock ++;
  }
}
