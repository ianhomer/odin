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

package com.purplepip.odin.composition.roll;

import com.purplepip.odin.composition.tick.Tick;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.runtime.Property;

/**
 * Events that occur over time.  It is called a roll in relation to the piano rolls in early
 * automatic pianos, which was a roll of paper with holes in to indicate when a note should
 * be played.
 */
public interface Roll<A> {
  Event<A> peek();

  Event<A> pop();

  /**
   * Move the roll to the specified tock position.
   *
   * @param tock tock position to set
   */
  void setTock(Whole tock);

  /**
   * Tick as a property.
   *
   * @return tick as a property
   */
  Property<Tick> getTick();

  /**
   * Whether this roll is empty.
   *
   * @return true if the roll is empty
   */
  boolean isEmpty();
}
