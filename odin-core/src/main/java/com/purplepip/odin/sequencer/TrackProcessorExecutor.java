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

package com.purplepip.odin.sequencer;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.NullValueEvent;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.sequencer.statistics.MutableSequenceProcessorStatistics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackProcessorExecutor implements Runnable {
  private final Things<Track> tracks;
  private final BeatClock clock;
  private final OperationProcessor operationProcessor;
  private long timeBufferInMicroSeconds;
  private int maxNotesPerBuffer;
  private int maxNotesPerBufferEarlyWarning;
  private MutableSequenceProcessorStatistics statistics;
  private MetricRegistry metrics;

  TrackProcessorExecutor(BeatClock clock,
                         Things<Track> tracks,
                         OperationProcessor operationProcessor,
                         long refreshPeriod,
                         int maxNotesPerBuffer,
                         MutableSequenceProcessorStatistics statistics,
                         MetricRegistry metrics) {
    this.clock = clock;
    this.tracks = tracks;
    this.operationProcessor = operationProcessor;
    this.metrics = metrics;
    /*
     * We need to scan forward more that the interval between executions of this processor.
     */
    timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
    this.maxNotesPerBuffer = maxNotesPerBuffer;
    maxNotesPerBufferEarlyWarning = maxNotesPerBuffer - 20;
    this.statistics = statistics;
  }

  /**
   * Run processor.
   */
  @Override
  public void run() {
    if (clock.isStarted()) {
      try {
        doJobWithTiming();
      } catch (RuntimeException e) {
        LOG.error("Error whilst executing sequence processing", e);
      }
    } else {
      if (clock.isStopped()) {
        LOG.debug("Clock has been stopped");
      } else {
        LOG.debug("Clock has not started yet");
      }
    }
  }

  // TODO : Can we use a metric annotation for this?
  private void doJobWithTiming() {
    try (Timer.Context jobTimerContext = metrics.timer("sequence.job").time()) {
      doJob();
    }
  }

  private void doJob() {
    int noteCountThisBuffer = 0;
    for (Track track : tracks) {
      try (Timer.Context context = metrics.timer("sequence.track." + track.getName()).time()) {
        LOG.trace("Processing track {}", track);
        if (noteCountThisBuffer > maxNotesPerBuffer) {
          LOG.warn("Too many notes in this buffer {} > {} ",
              noteCountThisBuffer, maxNotesPerBuffer);
          break;
        }
        noteCountThisBuffer += process(track);
      }
    }
    LOG.trace("Processed {} notes in {} tracks : {}", noteCountThisBuffer, tracks.size(), clock);
  }

  private int process(Track track) {
    metrics.meter("sequence.tracks").mark();
    int noteCount = 0;
    Event nextEvent = track.peek();

    if (nextEvent == null) {
      metrics.meter("sequence.noEvent").mark();
      return noteCount;
    }

    /*
     * Use a constant microsecond position for the whole loop to make it easier to debug
     * loop processing.  In this loop it is only used for setting forward scan windows and does
     * not need the precise microsecond positioning at the time of instruction execution.
     */
    long microsecondPosition = clock.getMicroseconds();
    long maxMicrosecondPosition = microsecondPosition + timeBufferInMicroSeconds;
    while (nextEvent != null
        && nextEvent.getTime().lt(Wholes.valueOf(maxMicrosecondPosition))) {
      if (noteCount > maxNotesPerBuffer) {
        LOG.warn("Too many notes in this buffer {} > {} before {}", noteCount,
            maxNotesPerBuffer, maxMicrosecondPosition);
        metrics.meter("sequence.bufferMaxed").mark();
        return noteCount;
      } else if (noteCount > maxNotesPerBufferEarlyWarning) {
        LOG.warn("Approaching max notes in buffer with : {} from {}", nextEvent, track.getName());
      }
      /*
       * Pop event to get it off the buffer.
       */
      nextEvent = track.pop();
      LOG.trace("Processing Event {}", nextEvent);
      if (nextEvent == null) {
        metrics.meter("sequence.nextEventNull").mark();
        // TODO : Understand why this can happen, it might be that another thread has beaten
        // this thread to it.  It occasionally happens in the tests.
        LOG.warn("Next event gone from stack, it was null when we popped it");
      } else if (nextEvent.getTime().lt(Wholes.valueOf(microsecondPosition))) {
        statistics.incrementEventTooLateCount();
        metrics.meter("sequence.tooLate").mark();
        LOG.warn("Skipping {}, too late to process  {} < {}", nextEvent,
            nextEvent.getTime(),
            microsecondPosition);
      } else if (nextEvent instanceof NullValueEvent) {
        /*
         * NullValueEvent check since conductors can swallow up events and such events should not
         * be sent to the processor.
         */
        LOG.trace("NullValueEvent ignored : {}", nextEvent);
        metrics.meter("sequence.nullEvent").mark();
      } else {
        sendToProcessor(nextEvent, track);
      }
      noteCount++;
      nextEvent = track.peek();
      LOG.trace("Next event {}", nextEvent);
    }

    if (nextEvent == null) {
      LOG.trace("No more events on sequence runtime");
    } else {
      LOG.trace("Next event {} is beyond horizon {} : clock {}",
          nextEvent, maxMicrosecondPosition, clock);
    }

    return noteCount;
  }

  private void sendToProcessor(Event event, Track sequenceTrack) {
    try {
      if (event.getValue() instanceof Note) {
        Note note = (Note) event.getValue();
        Operation noteOn = new NoteOnOperation(sequenceTrack.getChannel(),
            note.getNumber(), note.getVelocity());
        Operation noteOff = new NoteOffOperation(sequenceTrack.getChannel(),
            note.getNumber());
        operationProcessor.send(noteOn, event.getTime().floor());
        operationProcessor.send(noteOff, event.getTime().plus(note.getDuration()).floor());
        metrics.meter("sequence.sent.note").mark();
      } else if (event.getValue() instanceof Operation) {
        operationProcessor.send((Operation) event.getValue(), event.getTime().floor());
        metrics.meter("sequence.sent.operation").mark();
      } else {
        LOG.warn("Event not supported {}", event);
      }
    } catch (OdinException e) {
      LOG.error("Cannot handle operation to processor", e);
      metrics.meter("sequence.failure").mark();
    }
  }
}
