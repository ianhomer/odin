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

import com.purplepip.odin.math.Real;

/**
 * Tick converter.  A tick converter MUST be reversible such that time = convert(convertBack(time)
 * and duration = convertDuration(time, convertDurationBack(time, duration).  A tick convert
 * can therefore handle offsets of time series, but NOT looping of series.
 */
public interface TickConverter {
  Tick getTargetTick();

  Real convert(Real time);

  Real convertBack(Real time);

  /**
   * Convert duration at the given time.  Note that duration can be variable over time so
   * to accurately calculate duration the time at which the duration is to be calculated must
   * be provided.
   *
   * @param time time for when duration should be calculated
   * @param duration duration to convert
   * @return converted duration
   */
  Real convertDuration(Real time, Real duration);

  Real convertDurationBack(Real time, Real duration);
}
