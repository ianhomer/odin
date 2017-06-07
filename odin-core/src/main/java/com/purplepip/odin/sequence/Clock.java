package com.purplepip.odin.sequence;

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
  private MicrosecondPositionProvider microsecondPositionProvider;

  public Clock(BeatsPerMinute beatsPerMinute) {
    this(beatsPerMinute, new DefaultMicrosecondPositionProvider());
  }

  public Clock(BeatsPerMinute beatsPerMinute,
               MicrosecondPositionProvider microsecondPositionProvider) {
    this(beatsPerMinute, microsecondPositionProvider, 1);
  }

  /**
   * Create clock.
   *
   * @param beatsPerMinute beats per minute
   * @param microsecondPositionProvider microsecond provider
   * @param startRoundingFactor start rounding factor
   */
  public Clock(BeatsPerMinute beatsPerMinute,
               MicrosecondPositionProvider microsecondPositionProvider,
               long startRoundingFactor) {
    this.beatsPerMinute = beatsPerMinute;
    this.startRoundingFactor = startRoundingFactor;
    this.microsecondPositionProvider = microsecondPositionProvider;
  }

  /**
   * Start the clock.
   */
  public void start() {
    this.microsecondsPositionOfFirstBeat = startRoundingFactor
        * (microsecondPositionProvider.getMicrosecondPosition() / startRoundingFactor);
    LOG.debug("Starting clock at {}micros", microsecondsPositionOfFirstBeat);
  }

  /**
   * Get microsecond position.
   *
   * @return microsecond position
   */
  @Override
  public long getMicrosecondPosition() {
    return microsecondPositionProvider.getMicrosecondPosition() - microsecondsPositionOfFirstBeat;
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

}
