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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.properties.Observable;
import com.purplepip.odin.properties.Property;
import com.purplepip.odin.sequence.tick.Tick;
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
  }

  final void refresh() {
    forwards = new Direction(sourceTick.get(), targetTick.get());
    backwards = new Direction(targetTick.get(), sourceTick.get());
    LOG.debug("Refreshed {}", this);
  }

  @Override
  public Tick getTargetTick() {
    return targetTick.get();
  }

  Tick getSourceTick() {
    return sourceTick.get();
  }

  private long getSourceOffset() {
    return sourceOffset.get();
  }

  @Override
  public double convert(double time) {
    return convertTimeUnit(forwards, getSourceOffset() + time);
  }

  @Override
  public double convertBack(double time) {
    return convertTimeUnit(backwards, time) - getSourceOffset();
  }

  @Override
  public double convertDuration(double time, long duration) {
    return convert(time + duration) - convert(time);
  }

  @Override
  public double convertDurationBack(double time, long duration) {
    return convertBack(time + duration) - convertBack(time);
  }

  private double convertTimeUnit(Direction direction, double time) {
    switch (direction.getTargetTick().getTimeUnit()) {
      case BEAT:
        return getTimeWithBeatBasedTimeUnits(direction, time);
      case MICROSECOND:
        return getTimeWithMicrosecondBasedTimeUnits(direction, time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  protected abstract double getTimeWithBeatBasedTimeUnits(Direction direction, double time);

  protected abstract double getTimeWithMicrosecondBasedTimeUnits(Direction direction, double time);

  long throwUnexpectedTimeUnit() {
    throw new OdinRuntimeException("Unexpected time unit " + getSourceTick().getTimeUnit() + ":"
        + getTargetTick().getTimeUnit());
  }

  protected class Direction {
    private Tick sourceTick;
    private Tick targetTick;
    private double scaleFactor;

    private Direction(Tick sourceTick, Tick targetTick) {
      this.sourceTick = sourceTick;
      this.targetTick = targetTick;
      scaleFactor = sourceTick.getNumerator() * targetTick.getDenominator()
          / (double) (sourceTick.getDenominator() * targetTick.getNumerator());
      LOG.trace("{} to {} factor is {}", sourceTick, targetTick, scaleFactor);
    }

    protected Tick getSourceTick() {
      return sourceTick;
    }

    protected Tick getTargetTick() {
      return targetTick;
    }

    double scaleTime(double time) {
      return time * scaleFactor;
    }
  }
}
