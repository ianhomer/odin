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

package com.purplepip.odin.composition.clock;

import com.purplepip.odin.composition.MicrosecondPositionProvider;
import com.purplepip.odin.composition.tick.Tick;
import com.purplepip.odin.math.Real;

/**
 * A clock is aware of the chronological timing of each tick.
 */
public interface Clock extends MicrosecondPositionProvider {
  /**
   * Get the time unit for the ticks of this clock.
   *
   * @return tick time unit for clock
   */
  Tick getTick();

  /**
   * Get microsecond position for the given tick count.
   */
  long getMicroseconds(Real count);

  /**
   * Get tick count duration for the given number of microseconds from now.
   */
  Real getDuration(long microseconds);

  /**
   * Get tick count duration for the given number of microseconds form the given tick count.
   */
  Real getDuration(long microseconds, Real count);

  /**
   * Get the tick count for the current position.
   */
  Real getPosition();

  /**
   * Get the tick count for the given microsecond position.
   */
  Real getPosition(long microseconds);
}
