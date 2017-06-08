package com.purplepip.odin.sequence;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * SequenceRuntime clock that has the intelligence to know the timings of future beats.
 *
 * <p>Note that currently it is implemented with a static BPM, but note that the system in general
 * must support variable BPM, so it is essential that this Clock is the authority on timings of
 * each beat.
 */
@Slf4j
public class Clock implements MicrosecondPositionProvider {
  private BeatsPerMinute beatsPerMinute;
  private long microsecondsPositionOfFirstBeat;
  private long startRoundingFactor = 1;
  private long startOffset = 0;
  private MicrosecondPositionProvider microsecondPositionProvider;
  private boolean started = false;
  private List<ClockListener> listeners = new ArrayList<>();

  public Clock(BeatsPerMinute beatsPerMinute) {
    this(beatsPerMinute, new DefaultMicrosecondPositionProvider());
  }

  public Clock(BeatsPerMinute beatsPerMinute,
               MicrosecondPositionProvider microsecondPositionProvider) {
    this(beatsPerMinute, microsecondPositionProvider, 1);
  }

  public Clock(BeatsPerMinute beatsPerMinute,
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
  public Clock(BeatsPerMinute beatsPerMinute,
               MicrosecondPositionProvider microsecondPositionProvider,
               long startRoundingFactor,
               long startOffset) {
    this.beatsPerMinute = beatsPerMinute;
    this.startRoundingFactor = startRoundingFactor;
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.startOffset = startOffset;
  }

  public void addListener(ClockListener listener) {
    listeners.add(listener);
  }

  /**
   * Start the clock.
   */
  public void start() {
    /*
     * Round first beat up, to allow initial execution of processor to fire
     */
    this.microsecondsPositionOfFirstBeat = startRoundingFactor
        * ((startOffset + microsecondPositionProvider.getMicrosecondPosition())
        / startRoundingFactor);
    LOG.debug("Starting clock at {}micros", microsecondsPositionOfFirstBeat);
    started = true;
    listeners.forEach(ClockListener::onClockStart);
  }

  public void stop() {
    listeners.forEach(ClockListener::onClockStop);
  }

  /**
   * Get microsecond position.
   *
   * @return microsecond position
   */
  @Override
  public long getMicrosecondPosition() {
    return microsecondPositionProvider.getMicrosecondPosition();
  }

  public long getMicrosecondsPositionOfFirstBeat() {
    return microsecondsPositionOfFirstBeat;
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  double getBeat(long microseconds) {
    return (microseconds - microsecondsPositionOfFirstBeat)
        / (double) beatsPerMinute.getMicroSecondsPerBeat();
  }

  long getMicroSeconds(double beat) {
    return microsecondsPositionOfFirstBeat
        + (long) (beatsPerMinute.getMicroSecondsPerBeat() * beat);
  }

  public double getCurrentBeat() {
    return getBeat(microsecondPositionProvider.getMicrosecondPosition());
  }

  public boolean isStarted() {
    return started;
  }
}
