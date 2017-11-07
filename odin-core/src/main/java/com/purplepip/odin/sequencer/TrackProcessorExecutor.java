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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.bag.Things;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.NullValueEvent;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.sequence.clock.BeatClock;
import com.purplepip.odin.sequence.track.Track;
import com.purplepip.odin.sequencer.statistics.MutableSequenceProcessorStatistics;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackProcessorExecutor implements Runnable {
  private final Things<Track> tracks;
  private final BeatClock clock;
  private final OperationProcessor operationProcessor;
  private long timeBufferInMicroSeconds;
  private int maxNotesPerBuffer = 1000;
  private MutableSequenceProcessorStatistics statistics;
  private MetricRegistry metrics;
  private Timer jobMetric;
  private Meter tracksProcessedMetric;
  private Meter noEventsMetric;
  private Meter nextEventNullMetric;
  private Meter sentMetric;
  private Meter bufferMaxedMetric;
  private Meter tooLateMetric;
  private Meter nullEventMetric;
  private Meter failureMetric;
  private Map<String, Timer> trackTimers = new HashMap<>();

  TrackProcessorExecutor(BeatClock clock,
                         Things<Track> tracks,
                         OperationProcessor operationProcessor,
                         long refreshPeriod,
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
    this.statistics = statistics;
    jobMetric = metrics.timer("sequence.job");
    tracksProcessedMetric = metrics.meter("sequence.tracks");
    noEventsMetric = metrics.meter("sequence.noEvents");
    nextEventNullMetric = metrics.meter("sequence.nextEventNull");
    sentMetric = metrics.meter("sequence.sent");
    bufferMaxedMetric = metrics.meter("sequence.bufferMaxed");
    tooLateMetric = metrics.meter("sequence.tooLate");
    nullEventMetric = metrics.meter("sequence.nullEvent");
    failureMetric = metrics.meter("sequence.failure");
  }

  private Timer getTrackTimer(String name) {
    String metricName = "sequence.track." + name;
    if (trackTimers.containsKey(metricName)) {
      return trackTimers.get(metricName);
    }
    Timer timer = metrics.timer(metricName);
    trackTimers.put(metricName, timer);
    return timer;
  }

  /**
   * Run processor.
   */
  @Override
  public void run() {
    try {
      doJobWithTiming();
    } catch (RuntimeException e) {
      LOG.error("Error whilst executing sequence processing", e);
    }
  }

  private void doJob() {
    int noteCountThisBuffer = 0;
    for (Track track : tracks) {
      final Timer.Context trackTimerContext = getTrackTimer(track.getName()).time();
      try {
        LOG.trace("Processing track {}", track);
        if (noteCountThisBuffer > maxNotesPerBuffer) {
          LOG.warn("Too many notes in this buffer {} > {} ",
              noteCountThisBuffer, maxNotesPerBuffer);
          break;
        }
        noteCountThisBuffer += process(track);
      } finally {
        trackTimerContext.stop();
      }
    }
    LOG.debug("Processed {} notes in {} tracks : {}", noteCountThisBuffer, tracks.size(), clock);
  }

  // TODO : Can we use a metric annotation for this?
  private void doJobWithTiming() {
    final Timer.Context jobTimerContext = jobMetric.time();
    try {
      doJob();
    } finally {
      jobTimerContext.stop();
    }
  }

  private int process(Track track) {
    tracksProcessedMetric.mark();
    int noteCount = 0;
    Event<Note> nextEvent = track.peek();

    if (nextEvent == null) {
      LOG.debug("No event on roll");
      noEventsMetric.mark();
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
        && nextEvent.getTime().lt(Whole.valueOf(maxMicrosecondPosition))) {
      if (noteCount > maxNotesPerBuffer) {
        LOG.warn("Too many notes in this buffer {} > {} ", noteCount,
            maxNotesPerBuffer);
        bufferMaxedMetric.mark();
        return noteCount;
      }
      /*
       * Pop event to get it off the buffer.
       */
      nextEvent = track.pop();
      LOG.debug("Processing Event {}", nextEvent);
      if (nextEvent == null) {
        nextEventNullMetric.mark();
        // TODO : Understand why this can happen, it might be that another thread has beaten
        // this thread to it.  It occasionally happens in the tests.
        LOG.warn("Next event gone from stack, it was null when we popped it");
      } else if (nextEvent.getTime().lt(Whole.valueOf(microsecondPosition))) {
        statistics.incrementEventTooLateCount();
        tooLateMetric.mark();
        LOG.warn("Skipping event, too late to process  {} < {}", nextEvent.getTime(),
            microsecondPosition);
      } else if (nextEvent instanceof NullValueEvent) {
        /*
         * NullValueEvent check since conductors can swallow up events and such events should not
         * be sent to the processor.
         */
        LOG.trace("NullValueEvent ignored : {}", nextEvent);
        nullEventMetric.mark();
      } else {
        sendToProcessor(nextEvent.getValue(), nextEvent, track);
      }
      noteCount++;
      nextEvent = track.peek();
      LOG.trace("Next event {}", nextEvent);
    }

    if (nextEvent == null) {
      LOG.debug("No more events on sequence runtime");
    } else {
      LOG.debug("Next event {} is beyond horizon {} : clock {}",
          nextEvent, maxMicrosecondPosition, clock);
    }

    return noteCount;
  }

  private void sendToProcessor(Note note, Event<Note> nextEvent, Track sequenceTrack) {
    Operation noteOn = new NoteOnOperation(sequenceTrack.getChannel(),
        note.getNumber(), note.getVelocity());
    Operation noteOff = new NoteOffOperation(sequenceTrack.getChannel(),
        note.getNumber());
    try {
      LOG.trace("Sending note {} to channel {} at time {}",
          note, sequenceTrack.getChannel(), nextEvent.getTime());
      operationProcessor.send(noteOn, nextEvent.getTime().floor());
      operationProcessor.send(noteOff, nextEvent.getTime().plus(note.getDuration()).floor());
      sentMetric.mark();
    } catch (OdinException e) {
      LOG.error("Cannot send operation to processor", e);
      failureMetric.mark();
    }
  }
}
