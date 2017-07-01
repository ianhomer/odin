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

package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.tick.Tick;

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
  long getMicroseconds(long count);

  /**
   * Get microsecond position for the given tick count.
   */
  long getMicroseconds(double count);

  /**
   * Get the current tick count.
   */
  long getCount();

  /**
   * Get the tick count for the given microsecond position.
   */
  long getCount(long microseconds);

  /**
   * Get tick count duration for the given number of microseconds from now.
   */
  long getDuration(long microseconds);

  /**
   * Get tick count duration for the given number of microseconds form the given tick count.
   */
  long getDuration(long microseconds, long count);

  /**
   * Get the tick count for the given microsecond position.
   */
  double getCountAsDouble(long microseconds);
}
