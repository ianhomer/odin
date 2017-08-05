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

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.composition.CompositionBuilder;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Letter;
import com.purplepip.odin.music.notes.NoteNumber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EasyFlowCompositionListener extends EasyFlowBaseListener {
  private static final int DEFAULT_VELOCITY = 100;
  private CompositionBuilder builder = new CompositionBuilder();
  private Composition composition;

  private Letter letter;
  private int intonation = 0;
  private int octave;
  private int tock;
  private int velocity = DEFAULT_VELOCITY;

  public Composition getComposition() {
    return composition;
  }

  @Override
  public void exitComposition(EasyFlowParser.CompositionContext ctx) {
    builder.setLength(tock);
    composition = builder.create();
  }

  @Override
  public void enterLetter(EasyFlowParser.LetterContext ctx) {
    LOG.debug("Entering note {}", ctx.getText());
    letter = Letter.valueOf(ctx.getText());
    intonation = 0;
  }

  @Override
  public void enterOctave(EasyFlowParser.OctaveContext ctx) {
    octave = Integer.parseInt(ctx.getText());
  }

  @Override
  public void enterSharp(EasyFlowParser.SharpContext ctx) {
    intonation++;
  }

  @Override
  public void exitNote(EasyFlowParser.NoteContext ctx) {
    builder.addEvent(new DefaultEvent<>(
        new DefaultNote(
            new NoteNumber(letter, intonation, octave).getValue(),
            velocity, 1),
        tock));
    tock++;
  }
}
