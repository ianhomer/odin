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

import com.purplepip.odin.clock.tick.direction.DefaultDirection;
import com.purplepip.odin.clock.tick.direction.Direction;
import com.purplepip.odin.clock.tick.direction.UnreadyDirection;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.runtime.Observable;
import com.purplepip.odin.properties.runtime.Property;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for tick converters.
 */
@Slf4j
@ToString(exclude = {"backwards", "forwards"})
public abstract class AbstractTickConverter implements TickConverter {
  private Property<Long> sourceOffset;
  private Property<Tick> sourceTick;
  private Property<Tick> targetTick;
  private Direction forwards;
  private Direction backwards;

  final void setTargetTick(Property<Tick> targetTick) {
    this.targetTick = targetTick;
    if (targetTick instanceof Observable) {
      ((Observable) targetTick).addObserver(this::refresh);
    }
  }

  final void setSourceTick(Property<Tick> sourceTick) {
    this.sourceTick = sourceTick;
    if (sourceTick instanceof Observable) {
      ((Observable) sourceTick).addObserver(this::refresh);
    }
  }

  final void setSourceOffset(Property<Long> sourceOffset) {
    this.sourceOffset = sourceOffset;
    if (sourceOffset instanceof Observable) {
      ((Observable) sourceOffset).addObserver(this::refresh);
    }
  }

  final void refresh() {
    if (LOG.isDebugEnabled()) {
      if (forwards == null) {
        LOG.debug("Initial refresh {}", this);
      } else {
        LOG.debug("Refreshed {}", this);
      }
    }
    if (sourceTick.get() == null || targetTick.get() == null) {
      forwards = new UnreadyDirection(sourceTick.get(), targetTick.get());
      backwards = new UnreadyDirection(targetTick.get(), sourceTick.get());
    } else {
      forwards = new DefaultDirection(sourceTick.get(), targetTick.get());
      backwards = new DefaultDirection(targetTick.get(), sourceTick.get());
    }
  }

  @Override
  public Tick getTargetTick() {
    return targetTick.get();
  }

  Tick getSourceTick() {
    return sourceTick.get();
  }

  protected long getSourceOffset() {
    return sourceOffset.get();
  }

  @Override
  public Real convert(Real time) {
    return convertTimeUnit(forwards, Whole.valueOf(getSourceOffset()).plus(time));
  }

  @Override
  public Real convertBack(Real time) {
    return convertTimeUnit(backwards, time).minus(Whole.valueOf(getSourceOffset()));
  }

  @Override
  public Real convertDuration(Real time, Real duration) {
    return convert(time.plus(duration)).minus(convert(time));
  }

  @Override
  public Real convertDurationBack(Real time, Real duration) {
    return convertBack(time.plus(duration)).minus(convertBack(time));
  }

  private Real convertTimeUnit(Direction direction, Real time) {
    switch (direction.getTargetTick().getTimeUnit()) {
      case BEAT:
        return getTimeWithBeatBasedTimeUnits(direction, time);
      case MICROSECOND:
        return getTimeWithMicrosecondBasedTimeUnits(direction, time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  protected abstract Real getTimeWithBeatBasedTimeUnits(Direction direction, Real time);

  protected abstract Real getTimeWithMicrosecondBasedTimeUnits(Direction direction, Real time);

  Real throwUnexpectedTimeUnit() {
    throw new OdinRuntimeException("Unexpected time unit " + getSourceTick().getTimeUnit() + ":"
        + getTargetTick().getTimeUnit());
  }
}
