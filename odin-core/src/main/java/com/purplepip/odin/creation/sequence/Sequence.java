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

package com.purplepip.odin.creation.sequence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;

public interface Sequence extends SequenceConfiguration {
  /**
   * Get event <b>after</b> the given tock.  The sequence should not scan unnecessarily ahead since
   * the platform will take care of scanning, in general the sequence need not scan more than
   * whole tick number ahead.  In the future an event might be a collection of
   * simultaneous events after the given tock.  For now, however,  it is simply a single event.
   *
   * @param context flow context
   * @param loop time position at which to get the event
   * @return event at the given tock
   */
  Event getNextEvent(MeasureContext context, Loop loop);

  @JsonIgnore
  default Rational getLoopLength() {
    return getLength();
  }
}
