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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Reals;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.metrics.DefaultMetricRegistry;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
  private boolean starting;
  private boolean running;

  private Real maxLookForwardInMinutes;
  private long maxLookForwardInMicros;

  private MetricRegistry metrics;

  /**
   * Create beat clock.
   *
   * @param configuration beat clock configuration.
   */
  public BeatClock(Configuration configuration) {
    this.beatsPerMinute = configuration.beatsPerMinute();
    if (configuration.microsecondPositionProvider == null) {
      this.microsecondPositionProvider = new DefaultMicrosecondPositionProvider();
    } else {
      this.microsecondPositionProvider = configuration.microsecondPositionProvider();
    }
    this.startOffset = configuration.startOffset();
    this.maxLookForwardInMicros = configuration.maxLookForwardInMillis() * 1_000;
    maxLookForwardInMinutes = Reals.valueOf(
        configuration.maxLookForwardInMillis() / (double) 60_000);
    refreshMaxLookForward();
    metrics = configuration.metrics();
    if (metrics == null) {
      metrics = DefaultMetricRegistry.get();
    }
    LOG.debug("Creating beat clock : BPM = {}; ms provider = {}; look forward = {}",
        beatsPerMinute, microsecondPositionProvider, maxLookForwardInMicros);
  }

  /*
   * TODO : Support BPM change.
   */
  private void refreshMaxLookForward() {
    /*
     * Note we return a whole floor of this look forward since primary use case in flow always
     * compares against the floor.  Doing whole floor now gives a minor but cheap optimisation
     * and does not impact functionality in any substantial way.
     */
    setMaxLookForward(maxLookForwardInMinutes
        .times(Wholes.valueOf(beatsPerMinute.getBeatsPerMinute())).wholeFloor());
  }

  public void addListener(PerformanceListener listener) {
    boolean result = listeners.add(listener);
    LOG.debug("Clock listener {} added {} : 1 of {} listeners", listener, result, listeners.size());
  }

  /**
   * Prepare the clock ready for starting.
   */
  public void prepare() {
    try (Timer.Context context = metrics.timer("clock.prepare").time()) {
      listeners.forEach(PerformanceListener::onPerformancePrepare);
    }
  }

  /**
   * Start the clock.
   */
  public void start() {
    try (Timer.Context context = metrics.timer("clock.start").time()) {
      setMicroseconds(0);
      starting = true;
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
      running = true;
      starting = false;
    }
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
    running = false;
  }

  /**
   * Shutdown the clock and release resources.
   */
  public void shutdown() {
    listeners.forEach(PerformanceListener::onPerformanceShutdown);
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
    return Reals.valueOf((double)microseconds / beatsPerMinute.getMicroSecondsPerBeat().getValue());
  }

  public long getMaxLookForwardInMicros() {
    return maxLookForwardInMicros;
  }

  @Override
  public Real getMaxLookForward(Real position) {
    return maxLookForwardInMinutes.times(Wholes.valueOf(beatsPerMinute.getBeatsPerMinute()));
  }

  @Override
  public Tick getTick() {
    return Ticks.BEAT;
  }

  public boolean isStartingOrRunning() {
    return starting || running;
  }

  public boolean isRunning() {
    return running;
  }

  @Override public String toString() {
    return getMicroseconds() + "μs (beat="
        + new DecimalFormat("#.0").format(getPosition().getValue()) + ")";
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  @Accessors(fluent = true)
  public static final class Configuration {
    @Getter
    @Setter
    private BeatsPerMinute beatsPerMinute;

    @Getter
    @Setter
    private MicrosecondPositionProvider microsecondPositionProvider;

    /*
     * Start offset in microseconds.
     */
    @Getter
    @Setter
    private long startOffset = 1_000;

    @Getter
    @Setter
    private MetricRegistry metrics;

    @Getter
    @Setter
    private long maxLookForwardInMillis = 10_000;

    @Getter
    @Setter
    private boolean precision;

    public Configuration staticBeatsPerMinute(int staticBeatsPerMinute) {
      beatsPerMinute = new StaticBeatsPerMinute(staticBeatsPerMinute);
      return this;
    }
  }
}
