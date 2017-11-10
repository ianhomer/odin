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

package com.purplepip.odin.clock;

import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.composition.tick.Tick;
import com.purplepip.odin.composition.tick.Ticks;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
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
  private final MicrosecondPositionProvider microsecondPositionProvider;
  private final Set<ClockListener> listeners = new TreeSet<>(new ClockListenerComparator());
  private final BeatsPerMinute beatsPerMinute;

  private long microsecondsPositionOfFirstBeat;
  private long startRoundingFactor = 1;
  private final long startOffset;
  private boolean started;

  public static BeatClock newBeatClock(int staticBeatsPerMinute) {
    return newBeatClock(new StaticBeatsPerMinute(staticBeatsPerMinute));
  }

  public static BeatClock newBeatClock(BeatsPerMinute beatsPerMinute) {
    return new BeatClock(beatsPerMinute);
  }

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
    setMicroseconds(0);
    started = true;
    listeners.forEach(ClockListener::onClockStart);
  }

  /**
   * Set the clock.  When the clock starts this method is called with argument of 0 microseconds
   * indicating that the clock by default starts at 0 microseconds.  If you wish to scan
   * forward you can use this setMicroseconds method to set the current position of the beat
   * clock.
   *
   * @param microseconds microsecond position for the beat clock
   */
  public void setMicroseconds(long microseconds) {
    /*
     * Note that the first beat is never in the past, so the first beat timing needs to be
     * rounded up if necessary.  This rounding takes place after adding start offset.
     *
     * Note that (startRoundingFactor - 1) ensure we don't round up if we are already
     * on a rounding boundary.
     */
    long currentMicrosecondPosition = microsecondPositionProvider.getMicroseconds();
    microsecondsPositionOfFirstBeat = startRoundingFactor
        * ((startOffset + startRoundingFactor - 1 + currentMicrosecondPosition - microseconds)
        / startRoundingFactor);
    LOG.debug("Setting clock to {}μs in from first beat at {}μs", microseconds,
        microsecondsPositionOfFirstBeat);
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
  public long getMicroseconds(Real position) {
    return microsecondsPositionOfFirstBeat
        + position.times(beatsPerMinute.getMicroSecondsPerBeat()).floor();
  }

  @Override
  public Real getPosition() {
    return getPosition(microsecondPositionProvider.getMicroseconds());
  }

  @Override
  public Real getPosition(long microseconds) {
    return Whole.valueOf(microseconds - microsecondsPositionOfFirstBeat)
        .divide(beatsPerMinute.getMicroSecondsPerBeat());
  }

  @Override
  public Tick getTick() {
    return Ticks.BEAT;
  }

  public boolean isStarted() {
    return started;
  }

  @Override public String toString() {
    return getMicroseconds() + "μs (beat=" + getPosition() + ")";
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }
}
