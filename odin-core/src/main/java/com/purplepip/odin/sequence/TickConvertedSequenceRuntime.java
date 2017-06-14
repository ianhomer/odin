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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SequenceRuntime where time is in milliseconds relative to some origin, e.g. MIDI device start
 */
public class TickConvertedSequenceRuntime implements SequenceRuntime<Note> {
  private static final Logger LOG = LoggerFactory.getLogger(TickConvertedSequenceRuntime.class);
  private SequenceRuntime<Note> sequenceRuntime;
  private TickConverter tickConverter;

  public TickConvertedSequenceRuntime(SequenceRuntime<Note> sequenceRuntime,
                                      TickConverter tickConverter) {
    this.sequenceRuntime = sequenceRuntime;
    this.tickConverter = tickConverter;
  }

  @Override
  public Event<Note> peek() {
    return convertTimeUnits(sequenceRuntime.peek());
  }

  @Override
  public Event<Note> pop() {
    return convertTimeUnits(sequenceRuntime.pop());
  }

  @Override
  public Tick getTick() {
    return tickConverter.getOutputTick();
  }

  private Event<Note> convertTimeUnits(Event<Note> event) {
    if (event == null) {
      LOG.debug("No event on sequenceRuntime to convert");
      return null;
    }
    if (tickConverter.getOutputTick().equals(sequenceRuntime.getTick())) {
      return event;
    }
    Note note = new DefaultNote(event.getValue().getNumber(), event.getValue().getVelocity(),
        tickConverter.convertDuration(event.getTime(), event.getValue().getDuration()));
    long time = tickConverter.convert(event.getTime());
    LOG.trace("Converted note {} to time {}", note, time);
    return new DefaultEvent<>(note, time);
  }

}
