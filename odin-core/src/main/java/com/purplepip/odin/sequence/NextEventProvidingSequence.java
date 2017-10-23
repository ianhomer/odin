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

package com.purplepip.odin.sequence;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.sequence.flow.FlowContext;
import com.purplepip.odin.sequence.flow.Loop;

public interface NextEventProvidingSequence<A> extends Sequence {
  /**
   * Get event after the given tock, but not more than whole number ahead.  In the future an
   * event might be a collection of
   * simultaneous events after the given tock, however for now it is simply a single event.
   * This method will be moved onto sequence interface shortly.
   *
   * @param context flow context
   * @param loop time position at which to get the event
   * @return event at the given tock
   */
  Event<A> getNextEvent(FlowContext context, Loop loop);

  default Rational getLoopLength() {
    return Whole.valueOf(getLength());
  }
}
