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

package com.purplepip.odin.music.flow;

import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.flow.Loop;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Pattern flow.
 */
@FlowDefinition(name = "pattern", sequence = Pattern.class)
public class PatternFlow extends AbstractFlow<Pattern, Note> {
  public PatternFlow(Clock clock, MeasureProvider measureProvider) {
    super(clock, measureProvider);
  }

  @Override
  public Event<Note> getNextEvent(FlowContext context, Loop loop) {
    Real nextTock = loop.getPosition().getLimit().plus(Wholes.ONE);
    long countInMeasure = getContext().getMeasureProvider()
        .getCount(nextTock).floor();
    if (getSequence().getBits() == -1 || ((getSequence().getBits() >> countInMeasure) & 1) == 1)  {
      return new DefaultEvent<>(getSequence().getNote(), nextTock);
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return getSequence().getBits() == 0;
  }
}
