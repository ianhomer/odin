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

package com.purplepip.odin.clock.measure;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;

/**
 * A simple static measure provider for the beats from the clock start
 * , e.g. for music this is a static time signature.
 */
public class StaticBeatMeasureProvider implements MeasureProvider {
  private final Real beatsPerMeasure;

  public static StaticBeatMeasureProvider newMeasureProvider(int staticBeatsPerMeasure) {
    return new StaticBeatMeasureProvider(staticBeatsPerMeasure);
  }

  public StaticBeatMeasureProvider(int beatsPerMeasure) {
    this.beatsPerMeasure = Wholes.valueOf(beatsPerMeasure);
  }

  @Override
  public Real getMeasure(Real count) {
    return count.divide(beatsPerMeasure);
  }

  /**
   * Get tick in the measure where the given tock lies.
   *
   * @param count current count
   * @return ticks in the given measure
   */
  @Override
  public Real getTicksInMeasure(Real count) {
    return beatsPerMeasure;
  }

  @Override
  public Real getCount(Real count) {
    return count.modulo(beatsPerMeasure);
  }

  @Override
  public Real getNextMeasureStart(Real count) {
    return count.floor(beatsPerMeasure).plus(beatsPerMeasure);
  }
}
