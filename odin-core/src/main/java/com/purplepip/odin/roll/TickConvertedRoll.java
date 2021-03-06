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

package com.purplepip.odin.roll;

import static com.purplepip.odin.math.LessThan.lessThan;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Bound;
import com.purplepip.odin.math.LessThan;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.properties.runtime.Property;
import lombok.extern.slf4j.Slf4j;

/**
 * Roll calculated from another roll with the tick units converted.  Note that this tick
 * unit conversion can include offset adjustment as well as time unit conversion.
 */
@Slf4j
public class TickConvertedRoll implements Roll {
  private final Roll roll;
  private final TickConverter tickConverter;

  /**
   * Create tick converted roll.
   *
   * @param roll roll to convert
   * @param tickConverter tick converter to convert the runtime with
   */
  public TickConvertedRoll(Roll roll,
                           TickConverter tickConverter) {
    this.roll = roll;
    this.tickConverter = tickConverter;
  }

  @Override
  public Event peek() {
    return convertTimeUnits(roll.peek());
  }

  @Override
  public Event pop() {
    return convertTimeUnits(roll.pop());
  }

  @Override
  public void setTock(Bound tock) {
    /*
     * Convert the tock into units of the underlying roll before setting it.
     */
    Real convertedTock = tickConverter.convertBack(tock.getLimit()).wholeFloor();
    if (tock instanceof LessThan) {
      roll.setTock(lessThan(convertedTock));
    } else {
      roll.setTock(convertedTock);
    }
  }

  @Override
  public Property<Tick> getTick() {
    return tickConverter::getTargetTick;
  }

  @Override
  public boolean isEmpty() {
    return roll.isEmpty();
  }

  @Override
  public String getName() {
    return roll.getName();
  }

  @Override
  public void start() {
    LOG.warn("Redundant operation for start on {}", this);
  }

  @Override
  public void stop() {
    LOG.warn("Redundant operation for stop on {}", this);
  }

  @Override
  public void initialise() {
    LOG.warn("Redundant operation for initialise on {}", this);
  }

  @Override
  public void setProperty(String name, String value) {
    roll.setProperty(name, value);
  }

  @Override
  public String getProperty(String name) {
    return roll.getProperty(name);
  }

  private Event convertTimeUnits(Event event) {
    if (event == null) {
      LOG.trace("No event on roll {} to convert", roll.getName());
      return null;
    }
    Object convertedValue = event.getValue();
    if (event.getValue() instanceof Note) {
      Note original = (Note) event.getValue();
      convertedValue = new DefaultNote(original.getNumber(), original.getVelocity(),
          tickConverter.convertDuration(event.getTime(), original.getDuration()));
    }

    return new DefaultEvent(convertedValue, tickConverter.convert(event.getTime()).floor());
  }
}
