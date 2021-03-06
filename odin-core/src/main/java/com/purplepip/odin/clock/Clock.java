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

package com.purplepip.odin.clock;

import com.purplepip.odin.clock.tick.Tick;
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
   * Get tick count duration for the given number of microseconds from the given tick count.
   */
  Real getDuration(long microseconds, Real position);

  /**
   * Get the tick count for the current clock position.
   */
  Real getPosition();

  /**
   * Get the tick count for the given microsecond position.
   */
  Real getPosition(long microseconds);

  /**
   * Get max look forward, measured in ticks, from the current clock position.
   *
   * @return max look forward
   */
  Real getMaxLookForward();

  /**
   * Get max look forward, measured in ticks, from the given tick count. Max look forward is the
   * maximum number of ticks that a component can look forward pre-calculate events.
   */
  Real getMaxLookForward(Real position);
}
