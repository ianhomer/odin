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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.composition.Composition;
import com.purplepip.odin.music.composition.Measure;
import com.purplepip.odin.music.composition.Staff;
import com.purplepip.odin.music.composition.Voice;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.NoteNameCache;

/**
 * Write a composition as easy score notation.
 */
public class EasyScoreCompositionVisitor {
  private static final String REST_NOTE = "B4";

  private Composition composition;
  private StringBuilder builder;
  private NoteNameCache noteNameCache = new NoteNameCache();
  private boolean noteWritten = false;
  private EasyScoreNotationReference reference = new EasyScoreNotationReference();

  private Measure currentMeasure;
  private Rational lastNoteDuration;
  private Rational currentMeasurePosition;


  public EasyScoreCompositionVisitor(Composition composition) {
    this.composition = composition;
  }

  private void setCurrentMeasure(Measure measure) {
    currentMeasure = measure;
  }

  /**
   * Visit the composition objects and set appropriate notations.
   */
  public Composition visit() {
    composition.stream().forEachOrdered(this::visit);
    return composition;
  }

  private void visit(Measure measure) {
    setCurrentMeasure(measure);
    measure.stream().forEachOrdered(this::visit);
  }

  private void visit(Staff staff) {
    staff.stream().forEachOrdered(this::visit);
  }

  private void visit(Voice voice) {
    currentMeasurePosition = Wholes.ZERO;
    builder = new StringBuilder(128);
    voice.stream().forEachOrdered(this::visit);
    Whole expectedBeats = Real.valueOf(currentMeasure.getTime().getNumerator());
    if (!currentMeasurePosition.equals(expectedBeats)) {
      Rational remainingBeats = expectedBeats.minus(currentMeasurePosition);
      remainingBeats.getEgyptianFractions()
          .forEachOrdered(
              duration -> builder.append(", ").append(REST_NOTE)
                  .append(reference.getDurationLabel(duration)).append("/r"));
    }
    voice.setNotation(builder.toString());
  }

  private void visit(Event event) {
    Object value = event.getValue();
    if (value instanceof Note) {
      Note note = (Note) value;
      if (noteWritten) {
        builder.append(", ");
      }
      currentMeasurePosition = currentMeasurePosition.plus(note.getDuration()).toRational();
      builder.append(calculateNoteNotation(note));
      if (lastNoteDuration == null || !note.getDuration().equals(lastNoteDuration)) {
        if (note.getDuration() instanceof Rational) {
          Rational rationalNoteDuration = (Rational) note.getDuration();
          builder.append(reference.getDurationLabel(rationalNoteDuration));
          lastNoteDuration = rationalNoteDuration;
        } else {
          throw new OdinRuntimeException(
              "Note duration is not a rational number : " + note.getDuration());
        }
      }
      noteWritten = true;
    } else {
      throw new OdinRuntimeException("Can only create EasyScore notation for note events");
    }
  }

  private String calculateNoteNotation(Note note) {
    return noteNameCache.getName(note.getNumber());
  }
}
