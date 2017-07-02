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
import com.purplepip.odin.sequence.tick.RuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTickProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for tick converters.
 */
@Slf4j
public abstract class AbstractTickConverter implements TickConverter {
  private OffsetProvider sourceOffsetProvider;
  private RuntimeTickProvider sourceTickProvider;
  private RuntimeTickProvider targetTickProvider;
  private Direction forwards;
  private Direction backwards;

  final void setTargetTickProvider(RuntimeTickProvider targetTickProvider) {
    this.targetTickProvider = targetTickProvider;
  }

  final void setSourceTickProvider(RuntimeTickProvider sourceTickProvider) {
    this.sourceTickProvider = sourceTickProvider;
  }

  final void setSourceOffsetProvider(OffsetProvider inputOffsetProvider) {
    this.sourceOffsetProvider = inputOffsetProvider;
  }

  void afterPropertiesSet() {
    forwards = new Direction(sourceTickProvider.getTick(), targetTickProvider.getTick());
    backwards = new Direction(targetTickProvider.getTick(), sourceTickProvider.getTick());
  }

  @Override
  public RuntimeTick getTargetTick() {
    return targetTickProvider.getTick();
  }

  RuntimeTick getSourceTick() {
    return sourceTickProvider.getTick();
  }

  private long getSourceOffset() {
    return sourceOffsetProvider.getOffset();
  }

  @Override
  public long convert(long time) {
    return (long) convertTimeUnit(forwards, getSourceOffset() + time);
  }

  @Override
  public long convertBack(long time) {
    return (long) convertTimeUnit(backwards, time) - getSourceOffset();
  }

  @Override
  public long convertDuration(long time, long duration) {
    return convert(time + duration) - convert(time);
  }

  @Override
  public long convertDurationBack(long time, long duration) {
    return convertBack(time + duration) - convertBack(time);
  }

  private double convertTimeUnit(Direction direction, long time) {
    switch (direction.getTargetTick().getTimeUnit()) {
      case BEAT:
        return getTimeWithBeatBasedTimeUnits(direction, time);
      case MICROSECOND:
        return getTimeWithMicrosecondBasedTimeUnits(direction, time);
      default:
        return throwUnexpectedTimeUnit();
    }
  }

  protected abstract double getTimeWithBeatBasedTimeUnits(Direction direction, long time);

  protected abstract double getTimeWithMicrosecondBasedTimeUnits(Direction direction, long time);

  long throwUnexpectedTimeUnit() {
    throw new OdinRuntimeException("Unexpected time unit " + getSourceTick().getTimeUnit() + ":"
        + getTargetTick().getTimeUnit());
  }

  protected class Direction {
    private RuntimeTick sourceTick;
    private RuntimeTick targetTick;
    private double scaleFactor;

    private Direction(RuntimeTick sourceTick, RuntimeTick targetTick) {
      this.sourceTick = sourceTick;
      this.targetTick = targetTick;
      scaleFactor = sourceTick.getNumerator() * targetTick.getDenominator()
          / (double) (sourceTick.getDenominator() * targetTick.getNumerator());
      LOG.trace("{} to {} factor is {}", sourceTick, targetTick, scaleFactor);
    }

    protected RuntimeTick getSourceTick() {
      return sourceTick;
    }

    protected RuntimeTick getTargetTick() {
      return targetTick;
    }

    double scaleTime(long time) {
      return time * scaleFactor;
    }
  }
}
