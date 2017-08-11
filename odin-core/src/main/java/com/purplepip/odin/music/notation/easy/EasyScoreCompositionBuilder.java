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

package com.purplepip.odin.music.notation.easy;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.composition.events.EventsComposition;
import com.purplepip.odin.music.composition.events.EventsMeasure;
import com.purplepip.odin.music.composition.events.EventsStaff;
import com.purplepip.odin.music.composition.events.EventsVoice;
import com.purplepip.odin.music.notation.Reference;
import com.purplepip.odin.music.notation.easy.composition.EasyComposition;
import com.purplepip.odin.music.notation.easy.composition.EasyMeasure;
import com.purplepip.odin.music.notation.easy.composition.EasyStaff;
import com.purplepip.odin.music.notation.easy.composition.EasyVoice;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.NoteNameCache;
import java.util.ArrayList;
import java.util.List;

/**
 * Write a composition as easy score notation.
 */
public class EasyScoreCompositionBuilder {
  private static final String REST_NOTE = "B4";

  private EventsComposition eventsComposition;
  private List<EasyMeasure> easyMeasures = new ArrayList<>();
  private StringBuilder builder;
  private NoteNameCache noteNameCache = new NoteNameCache();
  private boolean noteWritten = false;
  private Reference reference = new EasyScoreReference();

  private EasyMeasure currentMeasure;
  private EasyStaff currentStaff;

  private Rational lastNoteDuration;
  private Rational currentMeasurePosition;


  public EasyScoreCompositionBuilder(EventsComposition composition) {
    this.eventsComposition = composition;
  }

  /**
   * Visit the composition objects and set appropriate notations.
   */
  public EasyComposition build() {
    eventsComposition.stream().forEachOrdered(this::visit);
    return new EasyComposition(easyMeasures);
  }

  private void startCurrentMeasure(EasyMeasure measure) {
    currentMeasure = measure;
    easyMeasures.add(measure);
  }

  private EasyMeasure createEasyMeasure(EventsMeasure measure) {
    return new EasyMeasure(measure.getTime(), measure.getKey());
  }

  private void startCurrentStaff(EasyStaff staff) {
    currentStaff = staff;
    currentMeasure.addStaff(staff);
  }

  private EasyStaff createEasyStaff(EventsStaff staff) {
    return new EasyStaff(staff.getClef());
  }

  private void visit(EventsMeasure measure) {
    startCurrentMeasure(createEasyMeasure(measure));
    measure.stream().forEachOrdered(this::visit);
  }

  private void visit(EventsStaff staff) {
    startCurrentStaff(createEasyStaff(staff));
    staff.stream().forEachOrdered(this::visit);
  }

  private void visit(EventsVoice voice) {
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
    currentStaff.addVoice(new EasyVoice(builder.toString()));
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
