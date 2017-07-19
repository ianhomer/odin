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

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.properties.Property;
import com.purplepip.odin.sequence.tick.Tick;
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
  public Property<Tick> getTick() {
    return tickConverter::getTargetTick;
  }

  private Event<Note> convertTimeUnits(Event<Note> event) {
    if (event == null) {
      LOG.debug("No event on roll {} to convert", roll);
      return null;
    }
    Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
        (long) tickConverter.convertDuration(event.getTime(), event.getValue().getDuration()));
    long time = (long) tickConverter.convert(event.getTime());
    LOG.trace("Converted event {} to note {} at time {}", event, note, time);
    return new DefaultEvent<>(note, time);
  }
}
