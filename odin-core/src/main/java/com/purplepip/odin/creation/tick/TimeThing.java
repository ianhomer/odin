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

package com.purplepip.odin.creation.tick;

import com.purplepip.odin.bag.Thing;
import javax.validation.constraints.NotNull;

/**
 * A thing that is time dependent.
 */
public interface TimeThing extends Thing {
  /**
   * Whether this thing is enabled by default.  Note that triggers can enable the thing.
   *
   * @return whether thing is enabled by default
   */
  boolean isEnabled();

  /**
   * Units for 1 tick of this thing.
   *
   * @return tick units.
   */
  @NotNull
  Tick getTick();

  /**
   * Length of this thin in ticks from the offset point.  Note that thing will stop
   * at offset + length ticks.
   *
   * @return length
   */
  /*
   * TODO : Change length from long to Rational
   */
  long getLength();

  /**
   * Offset for the start of this thing.
   *
   * @return offset
   */
  /*
   * TODO : Change offset from long to Rational
   */
  long getOffset();
}
