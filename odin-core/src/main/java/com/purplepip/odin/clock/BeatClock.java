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
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;

/**
 * Performance clock that has the intelligence to know the timings of future beats.  First beat
 * of the performance is always 0 microseconds.
 *
 * <p>Note that currently it is implemented with a static BPM, but note that the system in general
 * must support variable BPM, so it is essential that this Clock is the authority on timings of
 * each beat.</p>
 */
@Slf4j
public class BeatClock extends AbstractClock {
  private final MicrosecondPositionProvider microsecondPositionProvider;
  private final Set<PerformanceListener> listeners = new TreeSet<>(new ClockListenerComparator());
  private final BeatsPerMinute beatsPerMinute;

  private long microsecondsOffset;
  private long startOffset;
  private boolean started;
  private boolean startingOrStarted;
  private Real maxLookForwardInMinutes;
  private long maxLookForwardInMicros;

  public static BeatClock newBeatClock(int staticBeatsPerMinute) {
    return newBeatClock(new StaticBeatsPerMinute(staticBeatsPerMinute));
  }

  public static BeatClock newBeatClock(BeatsPerMinute beatsPerMinute) {
    return new BeatClock(beatsPerMinute);
  }

  public static BeatClock newBeatClock(int staticBeatsPerMinute,
                                       MicrosecondPositionProvider microsecondPositionProvider) {
    return newBeatClock(new StaticBeatsPerMinute(staticBeatsPerMinute),
        microsecondPositionProvider);
  }

  public static BeatClock newBeatClock(BeatsPerMinute beatsPerMinute,
                                       MicrosecondPositionProvider microsecondPositionProvider) {
    return new BeatClock(beatsPerMinute, microsecondPositionProvider);
  }

  public BeatClock(BeatsPerMinute beatsPerMinute) {
    this(beatsPerMinute, new DefaultMicrosecondPositionProvider());
  }

  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider) {
    this(beatsPerMinute, microsecondPositionProvider, 0);
  }

  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider,
                   long startOffset) {
    this(beatsPerMinute, microsecondPositionProvider, startOffset, 10_000);
  }

  /**
   * Create clock.
   *
   * @param beatsPerMinute beats per minute
   * @param microsecondPositionProvider microsecond provider
   */
  public BeatClock(BeatsPerMinute beatsPerMinute,
                   MicrosecondPositionProvider microsecondPositionProvider,
                   long startOffset, long maxLookForwardInMillis) {
    this.beatsPerMinute = beatsPerMinute;
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.startOffset = startOffset;
    this.maxLookForwardInMicros = maxLookForwardInMillis * 1_000;
    maxLookForwardInMinutes = Real.valueOf(maxLookForwardInMillis / (double) 60_000);
    refreshMaxLookForward();
    LOG.debug("Creating beat clock : look forward = {}", maxLookForwardInMicros);
  }

  /*
   * TODO : Support BPM change.
   */
  private void refreshMaxLookForward() {
    setMaxLookForward(maxLookForwardInMinutes
        .times(Whole.valueOf(beatsPerMinute.getBeatsPerMinute())));
  }

  public void addListener(PerformanceListener listener) {
    boolean result = listeners.add(listener);
    LOG.debug("Clock listener {} added {} : 1 of {} listeners", listener, result, listeners.size());
  }

  /**
   * Prepare the clock ready for starting.
   */
  public void prepare() {
    listeners.forEach(PerformanceListener::onPerformancePrepare);
  }

  /**
   * Start the clock.
   */
  public void start() {
    setMicroseconds(0);
    startingOrStarted = true;
    LOG.debug("Starting clock in {}μs : now = {}", startOffset, this);
    listeners.forEach(PerformanceListener::onPerformanceStart);
    LOG.debug("... started clock : offset = {}μs : now = {}", startOffset, this);
    if (getMicroseconds() > 0) {
      /*
       * If all performance listeners did not start before the clock reaches 0 microseconds
       * then there is a risk that some sequenced events did not fire in time.
       */
      LOG.warn("Clock listeners started slowly after clock start, please increase clock start"
          + " offset.  Current offset = {}, clock microseconds = {} > 0", startOffset,
          getMicroseconds());
    }
    started = true;
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
    microsecondsOffset = microsecondPositionProvider.getMicroseconds() + startOffset - microseconds;
    LOG.debug("Setting clock to {}μs in from first beat at {}μs", microseconds,
        microsecondsOffset);
  }

  /**
   * Stop the clock, although clock may be readily started again by a call to the start method.
   */
  public void stop() {
    listeners.forEach(PerformanceListener::onPerformanceStop);
    started = false;
  }

  /**
   * Shutdown the clock and release resources.
   */
  public void shutdown() {
    listeners.forEach(PerformanceListener::onPerformanceShutdown);
  }

  public long getMaxLookForwardInMicros() {
    return maxLookForwardInMicros;
  }

  /**
   * Get microsecond position.
   *
   * @return microsecond position
   */
  @Override
  public long getMicroseconds() {
    return microsecondPositionProvider.getMicroseconds() - microsecondsOffset;
  }

  @Override
  public long getMicroseconds(Real position) {
    return position.times(beatsPerMinute.getMicroSecondsPerBeat()).floor();
  }

  @Override
  public Real getPosition() {
    return getPosition(microsecondPositionProvider.getMicroseconds() - microsecondsOffset);
  }

  @Override
  public Real getPosition(long microseconds) {
    return Whole.valueOf(microseconds).divide(beatsPerMinute.getMicroSecondsPerBeat());
  }



  @Override
  public Real getMaxLookForward(Real position) {
    return maxLookForwardInMinutes.times(Whole.valueOf(beatsPerMinute.getBeatsPerMinute()));
  }

  @Override
  public Tick getTick() {
    return Ticks.BEAT;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isStartingOrStarted() {
    return startingOrStarted;
  }

  @Override public String toString() {
    return getMicroseconds() + "μs (beat="
        + new DecimalFormat("#.0").format(getPosition().getValue()) + ")";
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }
}
