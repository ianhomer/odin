/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.ClockListener;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.SequenceRuntime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SequenceRuntime processor.
 */
public class SequenceProcessor implements ClockListener {
  private static final Logger LOG = LoggerFactory.getLogger(SequenceProcessor.class);

  private final Set<SequenceTrack> sequenceTrackSet;
  private final Clock clock;
  private final OperationProcessor operationProcessor;
  private long refreshPeriod = 200;
  private long timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
  private int maxNotesPerBuffer = 1000;
  private ScheduledExecutorService scheduledPool;

  /**
   * Create a series processor.
   *
   * @param clock clock
   * @param sequenceTrackSet series track set
   * @param operationProcessor operation processor
   */
  SequenceProcessor(Clock clock,
                           Set<SequenceTrack> sequenceTrackSet,
                           OperationProcessor operationProcessor) {
    this.sequenceTrackSet = Collections.unmodifiableSet(sequenceTrackSet);
    this.clock = clock;
    this.operationProcessor = operationProcessor;
    clock.addListener(this);
    /*
     * If clock has already started then start immediately, otherwise wait for clock start
     * event.
     */
    if (clock.isStarted()) {
      start();
    }
  }

  @Override
  public void onClockStart() {
    start();
  }


  @Override
  public void onClockStop() {
    scheduledPool.shutdown();
  }

  private void start() {
    scheduledPool = Executors.newScheduledThreadPool(1);
    SequenceProcessorExecutor executor = new SequenceProcessorExecutor();
    scheduledPool.scheduleWithFixedDelay(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
  }

  /**
   * Close sequence processor.
   */
  public void close() {
    onClockStop();
  }

  class SequenceProcessorExecutor implements Runnable {

    /**
     * Run processor.
     */
    @Override
    public void run() {
      /*
       * Use a constant microsecond position for the whole loop to make it easier to debug
       * loop processing.  In this loop it is only used for setting forward scan windows and does
       * not need the precise microsecond positioning at the time of instruction execution.
       */
      long microsecondPosition = clock.getMicrosecondPosition();
      LOG.debug("Processing tracks : {}", microsecondPosition);
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
    }

    private int process(SequenceTrack sequenceTrack, long microsecondPosition) {
      int noteCount = 0;
      SequenceRuntime<Note> sequenceRuntime = sequenceTrack.getSequenceRuntime();
      if (sequenceRuntime.peek() != null) {
        Event<Note> nextEvent = sequenceRuntime.peek();
        while (nextEvent != null && nextEvent.getTime()
            < microsecondPosition + timeBufferInMicroSeconds) {
          if (noteCount > maxNotesPerBuffer) {
            LOG.warn("Too many notes in this buffer {} > {} ", noteCount,
                maxNotesPerBuffer);
            return noteCount;
          }
          /*
           * Pop event to get it off the buffer.
           */
          nextEvent = sequenceRuntime.pop();
          LOG.trace("Processing Event {}", nextEvent);
          if (nextEvent.getTime() < microsecondPosition) {
            LOG.warn("Skipping event, too late to process  {} : {} < {}",
                clock.getMicrosecondsPositionOfFirstBeat(), nextEvent.getTime(),
                microsecondPosition);
          } else {
            sendToProcessor(nextEvent.getValue(), nextEvent, sequenceTrack);
          }
          noteCount++;
          nextEvent = sequenceRuntime.peek();
          LOG.trace("Next event {}", nextEvent);
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
        LOG.debug("Sending note {} to channel {} at time {}",
            note, sequenceTrack.getChannel(), nextEvent.getTime());
        operationProcessor.send(noteOn, nextEvent.getTime());
        operationProcessor.send(noteOff, nextEvent.getTime() + note.getDuration());
      } catch (OdinException e) {
        LOG.error("Cannot send operation to processor", e);
      }
    }
  }


}
