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
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Easy flow composition listener for for easy flow (i.e. vexflow) notation parsing.
 */
/*
 * //TODO Handle key signature
 */
@Slf4j
public class EasyScoreCompositionListener extends EasyScoreBaseListener {
  private static final int DEFAULT_VELOCITY = 100;
  private static final Map<String, Integer> accidentals = new HashMap<>();

  static {
    accidentals.put("#", 1);
    accidentals.put("@", -1);
    accidentals.put("n", 0);
  }

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
  public void exitComposition(EasyScoreParser.CompositionContext ctx) {
    builder.setLength(tock);
    composition = builder.create();
  }

  @Override
  public void enterLetter(EasyScoreParser.LetterContext ctx) {
    LOG.debug("Entering note {}", ctx.getText());
    letter = Letter.valueOf(ctx.getText());
    intonation = 0;
  }

  @Override
  public void enterOctave(EasyScoreParser.OctaveContext ctx) {
    octave = Integer.parseInt(ctx.getText());
  }

  @Override
  public void enterAccidental(EasyScoreParser.AccidentalContext ctx) {
    intonation = intonation + accidentals.get(ctx.getText());
  }

  @Override
  public void exitNote(EasyScoreParser.NoteContext ctx) {
    builder.addEvent(new DefaultEvent<>(
        new DefaultNote(
            new NoteNumber(letter, intonation, octave).getValue(),
            velocity, 1),
        tock));
    tock++;
  }
}
