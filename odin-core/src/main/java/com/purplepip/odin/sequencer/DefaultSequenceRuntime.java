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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.AbstractMutableSequenceRuntime;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.flow.Flow;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime extends AbstractMutableSequenceRuntime<Sequence, Note> {
  private Flow<Sequence, Note> flow;

  /**
   * Create an instance of the default sequence runtime.
   *
   * @param clock clock
   * @param measureProvider measure provider
   * @param flow sequence flow
   */
  public DefaultSequenceRuntime(Clock clock, MeasureProvider measureProvider,
                                Flow<Sequence, Note> flow) {
    setClock(clock);
    setMeasureProvider(measureProvider);
    setSequence(flow.getSequence());
    this.flow = flow;
    initialise();
  }

  @Override
  protected Event<Note> getNextEvent(Tock tock) {
    return flow.getNextEvent(tock, getMeasureProvider());
  }
}
