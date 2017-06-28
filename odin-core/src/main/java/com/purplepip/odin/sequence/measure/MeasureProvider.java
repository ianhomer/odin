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
 * Intelligence on how the measures are defined over time.  For music a measure is bar and
 * for a given point in time a measure will have a will defined number of beats.
 *
 * For example the simple StaticMeasureProvider has a very predictable fixed number of beats in
 * each bar, so it can be easily calculated which measure we are in from the beat count.
 *
 * Generally, however, BPM might change at given points and not all bars may have the same
 * beat count, so more intelligent measure providers will be provided in the future.
 *
 * The measure provider must be able to cope with tocks of different units.
 * For example it must be able to answer the following questions.
 *
 * <ul>
 *   <li>How many eighth beats are in there in the measure 100 eighth beats from the start?</li>
 *   <li>How many beats are in there in the measure 60 seconds from the start?</li>
 *   <li>At 100 seconds from the starts, how many seconds are we from
 *   the beginning of the current measure?</li>
 * </ul>
 */
public interface MeasureProvider {
  /**
   * Get the measure that the tock is in.
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
   * What position in the measure is this tock?  0 =&gt; start of the measure.  Note that a tick
   * might be higher resolution than a single beat, e.g. it could be a half beat or a triplet.
   *
   * @param tock current tick
   * @return tick position in the current measure
   */
  long getTickPosition(Tock tock);
}
