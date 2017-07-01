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

import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;

/**
 * Primary clock that has the intelligence to know the timings of future beats.
 *
 * <p>Note that currently it is implemented with a static BPM, but note that the system in general
 * must support variable BPM, so it is essential that this Clock is the authority on timings of
 * each beat.</p>
 */
@Slf4j
public class BeatClock extends AbstractClock {
  private MicrosecondPositionProvider microsecondPositionProvider;
  private Set<ClockListener> listeners = new TreeSet<>(new ClockListenerComparator());
  private BeatsPerMinute beatsPerMinute;

  private long microsecondsPositionOfFirstBeat;
  private long startRoundingFactor = 1;
  private long startOffset;
  private boolean started;

  public BeatClock(BeatsPerMinute beatsPerMinute) {
    this(beatsPerMinute, new DefaultMicrosecondPositionProvider());
  }

  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider) {
    this(beatsPerMinute, microsecondPositionProvider, 1);
  }

  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider,
                   long startRoundingFactor) {
    this(beatsPerMinute, microsecondPositionProvider, startRoundingFactor, 0);
  }

  /**
   * Create clock.
   *
   * @param beatsPerMinute beats per minute
   * @param microsecondPositionProvider microsecond provider
   * @param startOffset how many microseconds to start in
   * @param startRoundingFactor start rounding factor
   */
  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider,
                   long startRoundingFactor,
                   long startOffset) {
    this.beatsPerMinute = beatsPerMinute;
    this.startRoundingFactor = startRoundingFactor;
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.startOffset = startOffset;
  }

  public void addListener(ClockListener listener) {
    boolean result = listeners.add(listener);
    LOG.debug("Clock listener {} added {} : 1 of {} listeners", listener, result, listeners.size());
  }

  /**
   * Start the clock.
   */
  public void start() {
    /*
     * Note that the first beat is never in the past, so the first beat timing needs to be
     * rounded up if necessary.  This rounding takes place after adding start offset.
     *
     * Note that (startRoundingFactor - 1) ensure we don't round up if we are already
     * on a rounding boundary.
     */
    long currentMicrosecondPosition = microsecondPositionProvider.getMicroseconds();
    this.microsecondsPositionOfFirstBeat = startRoundingFactor
        * ((startOffset + startRoundingFactor - 1 + currentMicrosecondPosition)
        / startRoundingFactor);
    LOG.debug("Starting clock at {}μs", microsecondsPositionOfFirstBeat);
    started = true;
    listeners.forEach(ClockListener::onClockStart);
  }

  /**
   * Stop the clock.
   */
  public void stop() {
    listeners.forEach(ClockListener::onClockStop);
  }

  /**
   * Get microsecond position.
   *
   * @return microsecond position
   */
  @Override
  public long getMicroseconds() {
    return microsecondPositionProvider.getMicroseconds();
  }

  @Override
  public long getMicroseconds(double count) {
    return microsecondsPositionOfFirstBeat
        + (long) (beatsPerMinute.getMicroSecondsPerBeat() * count);
  }

  @Override
  public long getMicroseconds(long count) {
    return microsecondsPositionOfFirstBeat
        + beatsPerMinute.getMicroSecondsPerBeat() * count;
  }

  public long getMicrosecondsOfFirstBeat() {
    return microsecondsPositionOfFirstBeat;
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  @Override
  public long getCount() {
    return getCount(microsecondPositionProvider.getMicroseconds());
  }

  @Override
  public long getCount(long microseconds) {
    return (long) getCountAsDouble(microseconds);
  }

  @Override
  public double getCountAsDouble(long microseconds) {
    return (microseconds - microsecondsPositionOfFirstBeat)
        / (double) beatsPerMinute.getMicroSecondsPerBeat();
  }

  @Override
  public Tick getTick() {
    return Ticks.BEAT;
  }

  public boolean isStarted() {
    return started;
  }

  @Override public String toString() {
    return getMicroseconds() + "μs (beat=" + (long) getCount() + ")";
  }
}
