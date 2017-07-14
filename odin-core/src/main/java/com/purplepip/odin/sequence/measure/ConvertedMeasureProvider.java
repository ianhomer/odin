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

import com.purplepip.odin.sequence.TickConverter;

/**
 * A measure provider that has been converted for a given time unit and clock offset for a
 * sequence.
 */
public class ConvertedMeasureProvider implements MeasureProvider {
  private MeasureProvider underlyingMeasureProvider;
  private TickConverter tickConverter;

  /**
   * Create a converted measure provider.  Note that the tick converter provider MUST convert
   * tocks from the tick of the underlying measure provider, e.g. sequencer measure provider
   * for the clock, to the destination tick, e.g. the tick and offset of a given sequence.
   *
   * @param measureProvider measure provider to convert from.
   * @param tickConverter tick converter to convert ticks
   */
  public ConvertedMeasureProvider(MeasureProvider measureProvider, TickConverter tickConverter) {
    underlyingMeasureProvider = measureProvider;
    this.tickConverter = tickConverter;
  }

  @Override
  public long getMeasure(double count) {
    return underlyingMeasureProvider.getMeasure(tickConverter.convertBack(count));
  }

  /**
   * Get beats in the measure where the given tock lies.
   *
   * @param count tock count
   * @return beast in the given measure
   */
  @Override
  public int getBeats(double count) {
    return underlyingMeasureProvider.getBeats(tickConverter.convertBack(count));
  }

  @Override
  public double getCount(double count) {
    double underlyingTickCount = underlyingMeasureProvider
        .getCount(tickConverter.convertBack(count));
    return tickConverter.convertDuration(count, underlyingTickCount);
  }
}
