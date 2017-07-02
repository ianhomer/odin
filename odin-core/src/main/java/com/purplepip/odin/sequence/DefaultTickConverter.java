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

import com.purplepip.odin.properties.Property;
import com.purplepip.odin.sequence.tick.RuntimeTick;
import lombok.ToString;

/**
 * Default tick converter.
 */
@ToString(callSuper = true)
public class DefaultTickConverter extends AbstractTickConverter {
  private BeatClock clock;

  /**
   * Create a tick converter.
   *
   * @param clock clock
   * @param sourceTick source tick property
   * @param targetTick target tick property
   * @param sourceOffset source offset property
   */
  public DefaultTickConverter(BeatClock clock,
                              Property<RuntimeTick> sourceTick,
                              Property<RuntimeTick> targetTick,
                              Property<Long> sourceOffset) {
    this.clock = clock;
    setSourceTick(sourceTick);
    setTargetTick(targetTick);
    setSourceOffset(sourceOffset);
    refresh();
  }

  @Override
  protected double getTimeWithBeatBasedTimeUnits(Direction direction, double time) {
    switch (direction.getSourceTick().getTimeUnit()) {
      case BEAT:
        return direction.scaleTime(time);
      case MICROSECOND:
        return clock.getCountAsDouble((long) (direction.getSourceTick().getFactor() * time))
            / direction.getTargetTick().getFactor();
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  @Override
  protected double getTimeWithMicrosecondBasedTimeUnits(Direction direction, double time) {
    switch (direction.getSourceTick().getTimeUnit()) {
      case BEAT:
        return clock.getMicroseconds(direction.getSourceTick().getFactor() * time)
            / direction.getTargetTick().getFactor();
      case MICROSECOND:
        return direction.scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }
}
