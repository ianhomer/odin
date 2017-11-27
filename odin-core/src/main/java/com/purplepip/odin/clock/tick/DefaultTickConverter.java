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

package com.purplepip.odin.clock.tick;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.tick.direction.Direction;
import com.purplepip.odin.common.OdinImplementationException;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.runtime.ObservableProperty;
import com.purplepip.odin.properties.runtime.Property;

/**
 * Default tick converter.
 */
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
                              Property<Tick> sourceTick,
                              Property<Tick> targetTick,
                              Property<Long> sourceOffset) {
    this(clock, sourceTick, targetTick, sourceOffset, true);
  }

  /**
   * Create a tick converter.
   *
   * @param clock clock
   * @param sourceTick source tick property
   * @param targetTick target tick property
   * @param sourceOffset source offset property
   * @param initialise whether to initialise this tick converter.  If this is false then the system
   *                will expect the property to be an observable property and for this property
   *                to change before the tick converter is used.
   */
  public DefaultTickConverter(BeatClock clock,
                              Property<Tick> sourceTick,
                              Property<Tick> targetTick,
                              Property<Long> sourceOffset,
                              boolean initialise) {
    this.clock = clock;
    setSourceTick(sourceTick);
    setTargetTick(targetTick);
    setSourceOffset(sourceOffset);
    if (initialise) {
      refresh();
    } else {
      if (!(sourceTick instanceof ObservableProperty)
          && !(targetTick instanceof ObservableProperty)) {
        throw new OdinImplementationException(
            "Source tick or target tick MUST be an ObservableProperty for lazy initialisation");
      }
    }
  }

  @Override
  protected Real getTimeWithBeatBasedTimeUnits(Direction direction, Real time) {
    switch (direction.getSourceTick().getTimeUnit()) {
      case BEAT:
        return direction.scaleTime(time);
      case MICROSECOND:
        return clock.getPosition(
            direction.getSourceTick().getFactor().times(time).floor())
              .divide(direction.getTargetTick().getFactor());
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  @Override
  protected Real getTimeWithMicrosecondBasedTimeUnits(Direction direction, Real time) {
    switch (direction.getSourceTick().getTimeUnit()) {
      case BEAT:
        return Whole.valueOf(clock.getMicroseconds(
            direction.getSourceTick().getFactor().times(time)))
                .divide(direction.getTargetTick().getFactor());
      case MICROSECOND:
        return direction.scaleTime(time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  /**
   * To String.
   *
   * @return string value
   */
  public String toString() {
    return "DefaultTickConverter(sourceOffset="
        + this.getSourceOffset() + ", sourceTick="
        + this.getSourceTick() + ", targetTick=" + this.getTargetTick() + ")";
  }
}
