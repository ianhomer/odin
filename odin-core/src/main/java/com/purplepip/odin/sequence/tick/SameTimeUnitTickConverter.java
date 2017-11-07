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

package com.purplepip.odin.sequence.tick;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.properties.runtime.Property;
import com.purplepip.odin.sequence.tick.AbstractTickConverter;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.TimeUnit;

/**
 * Tick converter that is not clock aware and can only convert ticks with the same time unit.
 */
public class SameTimeUnitTickConverter extends AbstractTickConverter {
  public SameTimeUnitTickConverter(Property<Tick> sourceTick,
                                   Property<Tick> targetTick) {
    this(sourceTick, targetTick, () -> 0L);
  }

  /**
   * Create same time unit tick converter.
   *
   * @param sourceTick source tick property
   * @param targetTick target tick property
   * @param sourceOffset input offset provider
   */
  public SameTimeUnitTickConverter(Property<Tick> sourceTick,
                                   Property<Tick> targetTick,
                                   Property<Long> sourceOffset) {
    setSourceTick(sourceTick);
    setTargetTick(targetTick);
    setSourceOffset(sourceOffset);
    refresh();
  }

  @Override
  protected Real getTimeWithBeatBasedTimeUnits(Direction direction, Real time) {
    if (getSourceTick().getTimeUnit() == TimeUnit.BEAT) {
      return direction.scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }

  @Override
  protected Real getTimeWithMicrosecondBasedTimeUnits(Direction direction, Real time) {
    if (getSourceTick().getTimeUnit() == TimeUnit.MICROSECOND) {
      return direction.scaleTime(time);
    }
    return throwUnexpectedTimeUnit();
  }
}
