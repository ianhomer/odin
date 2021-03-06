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

import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.EventsCompositionBuilder;
import com.purplepip.odin.music.notation.NaturalScoreBaseListener;
import com.purplepip.odin.music.notation.NaturalScoreParser;
import com.purplepip.odin.music.notation.Reference;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Letter;
import com.purplepip.odin.music.notes.NoteNumber;
import com.purplepip.odin.music.notes.Notes;
import lombok.extern.slf4j.Slf4j;

/**
 * Natural score composition listener for natural score notation parsing.  The creation
 * composition is guaranteed to always be at least one measure long.
 */
/*
 * //TODO Handle key signature
 */
@Slf4j
public class NaturalScoreCompositionListener extends NaturalScoreBaseListener {
  protected static final int MINIMUM_MEASURES = 1;
  private static final int DEFAULT_OCTAVE = 4;
  private EventsCompositionBuilder builder = new EventsCompositionBuilder()
      .withMinimumMeasures(MINIMUM_MEASURES);
  private EventsComposition composition;
  private Reference reference = new NaturalScoreReference();

  private Letter letter;
  private int intonation;
  private Real duration = Notes.DEFAULT_DURATION;
  private int octave = DEFAULT_OCTAVE;

  public EventsComposition getComposition() {
    return composition;
  }

  @Override
  public void exitComposition(NaturalScoreParser.CompositionContext context) {
    composition = builder.create();
  }

  @Override
  public void enterLetter(NaturalScoreParser.LetterContext context) {
    LOG.trace("Entering note {}", context.getText());
    letter = Letter.valueOf(context.getText());
    intonation = 0;
  }

  @Override
  public void enterOctave(NaturalScoreParser.OctaveContext context) {
    octave = Integer.parseInt(context.getText());
  }

  @Override
  public void enterAccidental(NaturalScoreParser.AccidentalContext context) {
    intonation = intonation + reference.getAccidentalIncrement(context.getText());
  }

  @Override
  public void enterDuration(NaturalScoreParser.DurationContext context) {
    duration = reference.getDurationLength(context.getText());
    LOG.trace("Entering duration {} = {}", context.getText(), duration);
  }

  @Override
  public void exitNote(NaturalScoreParser.NoteContext ctx) {
    int velocity = Notes.DEFAULT_VELOCITY;
    builder.addNote(new DefaultNote(
        new NoteNumber(letter, intonation, octave).getValue(), velocity, duration));
  }
}
