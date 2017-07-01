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

import com.purplepip.odin.sequence.tick.RuntimeTick;

/**
 * Tick converter that is not clock aware and can only convert ticks with the same time unit.
 */
public class SameTimeUnitTickConverter extends AbstractTickConverter {
  public SameTimeUnitTickConverter(RuntimeTick inputTick, RuntimeTick outputTick) {
    this(inputTick, outputTick, () -> 0);
  }

  /**
   * Create same time unit tick converter.
   *
   * @param inputTick input tick
   * @param outputTick output tick
   * @param inputOffsetProvider input offset provider
   */
  public SameTimeUnitTickConverter(RuntimeTick inputTick, RuntimeTick outputTick,
                                   OffsetProvider inputOffsetProvider) {
    setSourceTick(inputTick);
    setTargetTick(outputTick);
    setSourceOffsetProvider(inputOffsetProvider);
    afterPropertiesSet();
  }

  @Override
  protected double getTimeWithBeatBasedTimeUnits(Direction direction, long time) {
    if (getSourceTick().getTimeUnit() == TimeUnit.BEAT) {
      return direction.scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }

  @Override
  protected double getTimeWithMicrosecondBasedTimeUnits(Direction direction, long time) {
    if (getSourceTick().getTimeUnit() == TimeUnit.MICROSECOND) {
      return direction.scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }
}
