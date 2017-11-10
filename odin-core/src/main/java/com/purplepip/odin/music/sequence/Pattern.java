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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.composition.flow.FlowContext;
import com.purplepip.odin.composition.flow.Loop;
import com.purplepip.odin.composition.sequence.GenericSequence;
import com.purplepip.odin.composition.sequence.SpecialisedSequence;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Pattern sequence.
 */
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Name("pattern")
public class Pattern extends GenericSequence implements SpecialisedSequence {
  /*
   * Binary pattern for series, 1 => on first tick of bar, 3 => on first two ticks of bar etc.
   */
  private int bits;
  private Note note = Notes.newNote();

  public Pattern bits(int bits) {
    this.bits = bits;
    return this;
  }

  public Pattern note(Note note) {
    this.note = note;
    return this;
  }

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    Real nextTock = loop.getPosition().getLimit().plus(Wholes.ONE);
    long countInMeasure = context.getMeasureProvider().getCount(nextTock).floor();
    if (bits == -1 || ((bits >> countInMeasure) & 1) == 1)  {
      return new DefaultEvent<>(note, nextTock);
    }
    return null;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public Pattern copy() {
    return copy(new Pattern(), this);
  }

  protected Pattern copy(Pattern copy, Pattern original) {
    super.copy(copy, original);
    copy.bits = original.bits;
    copy.note = original.note;
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return bits == 0 || super.isEmpty();
  }
}
