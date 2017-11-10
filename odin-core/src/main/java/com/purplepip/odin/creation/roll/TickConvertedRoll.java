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

package com.purplepip.odin.creation.roll;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.events.DefaultEvent;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.DefaultNote;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.properties.runtime.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Roll calculated from another roll with the tick units converted.  Note that this tick
 * unit conversion can include offset adjustment as well as time unit conversion.
 */
public class TickConvertedRoll implements Roll<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(TickConvertedRoll.class);
  private Roll<Note> roll;
  private TickConverter tickConverter;

  /**
   * Create tick converted roll.
   *
   * @param roll roll to convert
   * @param tickConverter tick converter to convert the runtime with
   */
  public TickConvertedRoll(Roll<Note> roll,
                           TickConverter tickConverter) {
    this.roll = roll;
    this.tickConverter = tickConverter;
  }

  @Override
  public Event<Note> peek() {
    return convertTimeUnits(roll.peek());
  }

  @Override
  public Event<Note> pop() {
    return convertTimeUnits(roll.pop());
  }

  @Override
  public void setTock(Whole tock) {
    /*
     * Convert the tock into units of the underlying roll before setting it.
     */
    roll.setTock(tickConverter.convertBack(tock).wholeFloor());
  }

  @Override
  public Property<Tick> getTick() {
    return tickConverter::getTargetTick;
  }

  @Override
  public boolean isEmpty() {
    return roll.isEmpty();
  }

  private Event<Note> convertTimeUnits(Event<Note> event) {
    if (event == null) {
      LOG.debug("No event on roll {} to convert", roll);
      return null;
    }
    Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
        tickConverter.convertDuration(event.getTime(), event.getValue().getDuration()));
    long time = tickConverter.convert(event.getTime()).floor();
    LOG.debug("Converted event {} to note {} to time {}", event, note, time);
    return new DefaultEvent<>(note, time);
  }
}
