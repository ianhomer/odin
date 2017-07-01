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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.SequenceRuntime;
import com.purplepip.odin.sequencer.statistics.MutableSequenceProcessorStatistics;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceProcessorExecutor implements Runnable {
  private final Set<SequenceTrack> sequenceTrackSet;
  private final BeatClock clock;
  private final OperationProcessor operationProcessor;
  private long timeBufferInMicroSeconds;
  private int maxNotesPerBuffer = 1000;
  private MutableSequenceProcessorStatistics statistics;

  SequenceProcessorExecutor(BeatClock clock,
                            Set<SequenceTrack> sequenceTrackSet,
                            OperationProcessor operationProcessor,
                            long refreshPeriod,
                            MutableSequenceProcessorStatistics statistics) {
    this.clock = clock;
    this.sequenceTrackSet = sequenceTrackSet;
    this.operationProcessor = operationProcessor;
    /*
     * We need to scan forward more that the interval between executions of this processor.
     */
    timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
    this.statistics = statistics;
  }

  /**
   * Run processor.
   */
  @Override
  public void run() {
    try {
      doJob();
    } catch (RuntimeException e) {
      LOG.error("Error whilst executing sequence processing", e);
    }
  }

  private void doJob() {
    /*
     * Use a constant microsecond position for the whole loop to make it easier to debug
     * loop processing.  In this loop it is only used for setting forward scan windows and does
     * not need the precise microsecond positioning at the time of instruction execution.
     */
    long microsecondPosition = clock.getMicroseconds();
    int noteCountThisBuffer = 0;
    for (SequenceTrack sequenceTrack : sequenceTrackSet) {
      LOG.trace("Processing sequenceRuntime {} for device at position {}",
          sequenceTrack.getSequenceRuntime(),
          microsecondPosition);
      if (noteCountThisBuffer > maxNotesPerBuffer) {
        LOG.warn("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
            maxNotesPerBuffer);
        break;
      }
      noteCountThisBuffer += process(sequenceTrack, microsecondPosition);
    }
    LOG.debug("Processed {} notes in {} tracks : {}",
        noteCountThisBuffer, sequenceTrackSet.size(), clock);
  }

  private int process(SequenceTrack sequenceTrack, long microsecondPosition) {
    int noteCount = 0;
    SequenceRuntime<Note> sequenceRuntime = sequenceTrack.getSequenceRuntime();
    Event<Note> nextEvent = sequenceRuntime.peek();
    long maxMicrosecondPosition = microsecondPosition + timeBufferInMicroSeconds;
    if (nextEvent != null) {
      while (nextEvent != null && nextEvent.getTime() < maxMicrosecondPosition) {
        if (noteCount > maxNotesPerBuffer) {
          LOG.warn("Too many notes in this buffer {} > {} ", noteCount,
              maxNotesPerBuffer);
          return noteCount;
        }
        /*
         * Pop event to get it off the buffer.
         */
        nextEvent = sequenceRuntime.pop();
        LOG.debug("Processing Event {}", nextEvent);
        if (nextEvent.getTime() < microsecondPosition) {
          statistics.incrementEventTooLateCount();
          LOG.warn("Skipping event, too late to process  {} < {}", nextEvent.getTime(),
              microsecondPosition);
        } else {
          sendToProcessor(nextEvent.getValue(), nextEvent, sequenceTrack);
        }
        noteCount++;
        nextEvent = sequenceRuntime.peek();
        LOG.trace("Next event {}", nextEvent);
      }
      if (nextEvent == null) {
        LOG.debug("No more events on sequence runtime");
      } else {
        LOG.debug("Next event {} is beyond horizon {}", nextEvent, maxMicrosecondPosition);
      }
    } else {
      LOG.debug("No event on sequenceRuntime");
    }
    return noteCount;
  }

  private void sendToProcessor(Note note, Event<Note> nextEvent, SequenceTrack sequenceTrack) {
    Operation noteOn = new NoteOnOperation(sequenceTrack.getChannel(),
        note.getNumber(), note.getVelocity());
    Operation noteOff = new NoteOffOperation(sequenceTrack.getChannel(),
        note.getNumber());
    try {
      LOG.trace("Sending note {} to channel {} at time {}",
          note, sequenceTrack.getChannel(), nextEvent.getTime());
      operationProcessor.send(noteOn, nextEvent.getTime());
      operationProcessor.send(noteOff, nextEvent.getTime() + note.getDuration());
    } catch (OdinException e) {
      LOG.error("Cannot send operation to processor", e);
    }
  }
}
