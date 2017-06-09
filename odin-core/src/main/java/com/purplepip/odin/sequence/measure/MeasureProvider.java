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

package com.purplepip.odin.sequence.measure;

import com.purplepip.odin.sequence.Tock;

/**
 * Intelligence on what measure we are in.  For music a measure is bar, generically a measure
 * is collection of beats.
 */
public interface MeasureProvider {
  /**
   * Get the measure that the tock is in.
   *
   * @param tock Current tick
   * @return Current measure number
   */
  long getMeasureCountForTock(Tock tock);

  /**
   * Get the number of beats are in the measure for the given tock.
   *
   * @param tock current tick
   * @return beats in the current measure
   */
  int getBeatsInThisMeasure(Tock tock);

  /**
   * What position in the measure is this tock?  0 =&gt; start of the measure.  Note that a tick
   * might be higher resolution than a single beat, e.g. it could be a half beat or a triplet.
   *
   * @param tock current tick
   * @return tick position in the current measure
   */
  long getTickPositionInThisMeasure(Tock tock);
}
