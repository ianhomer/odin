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

package com.purplepip.odin.creation.flow;

import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.clock.tick.Tock;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.events.Event;

/**
 * A flow class has the intelligence to determine the next events in a sequence.
 */
public interface Flow<S extends SequenceConfiguration> {
  /**
   * Get the next event after the given tock.
   *
   * @param tock time position after which to start scanning
   * @return next event after the tock
   */
  Event getNextEvent(Tock tock);

  S getSequence();

  MeasureContext getContext();

  default boolean isEmpty() {
    return false;
  }

  default void initialise() {}
}
