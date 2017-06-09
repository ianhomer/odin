/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequence;

/**
 * Tick converter.
 */
public interface TickConverter {
  Tick getOutputTick();

  long convert(long time);

  /**
   * Convert duration at the given time.  Note that duration can be variable over time so
   * to accurately calculate duration the time at which the duration is to be calculated must
   * be provided.
   *
   * @param time time for when duration should be calculated
   * @param duration duration to convert
   * @return converted duration
   */
  long convertDuration(long time, long duration);
}
