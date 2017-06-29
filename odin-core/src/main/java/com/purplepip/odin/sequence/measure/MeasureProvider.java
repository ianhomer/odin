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

package com.purplepip.odin.sequence.measure;

import com.purplepip.odin.sequence.tick.Tock;

/**
 * <p>Intelligence on how the measures are defined over time.  For music a measure is bar and
 * for a given point in time a measure will have a will defined number of beats.
 * For example the simple StaticMeasureProvider has a very predictable fixed number of beats in
 * each bar, so it can be easily calculated which measure we are in from the beat count.
 * Generally, however, not all bars may have the same beat count, so more intelligent measure
 * providers will be provided in the future.</p>
 *
 * <p>Note that the measure provider is only beat dependent, NOT time dependent.  It says nothing
 * about the timings of the beats just which beat each measure starts and how long that measure.</p>
 */
/*
 * TODO : Handle offset, tock count starts at offset from the start of the clock which is not
 * currently taken into account here.
 */
public interface MeasureProvider {
  /**
   * Get the measure that the given tock is in.
   *
   * @param tock Current tick
   * @return Current measure number
   */
  long getMeasureCount(Tock tock);

  /**
   * Get the number of beats are in the measure for the given tock.
   *
   * @param tock current tick
   * @return beats in the current measure
   */
  int getBeats(Tock tock);

  /**
   * What position in the measure is the given tock?  0 =&gt; start of the measure.  Note that
   * a tick might be higher resolution than a single beat, e.g. it could be a half beat or a
   * triplet.
   *
   * @param tock current tick
   * @return tick position in the current measure
   */
  long getTickPosition(Tock tock);
}
