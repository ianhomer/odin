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
 * Tick converter.
 */
public interface TickConverter {
  Tick getTargetTick();

  default double convert(long time) {
    return convert((double) time);
  }

  double convert(double time);

  default double convertBack(long time) {
    return convertBack((double) time);
  }

  double convertBack(double time);

  default double convertDuration(long time, long duration) {
    return convertDuration((double) time, (double) duration);
  }

  /**
   * Convert duration at the given time.  Note that duration can be variable over time so
   * to accurately calculate duration the time at which the duration is to be calculated must
   * be provided.
   *
   * @param time time for when duration should be calculated
   * @param duration duration to convert
   * @return converted duration
   */
  double convertDuration(double time, double duration);

  default double convertDurationBack(long time, long duration) {
    return convertDurationBack((double) time, (double) duration);
  }

  // TODO change duration argument to double
  double convertDurationBack(double time, double duration);
}
