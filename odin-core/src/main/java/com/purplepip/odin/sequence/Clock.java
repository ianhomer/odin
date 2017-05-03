package com.purplepip.odin.sequence;

/**
 * Sequence clock that has the intelligence to know the timings of future beats.
 *
 * <p>Note that currently it is implemented with a static BPM, but note that the system in general
 * must support variable BPM, so it is essential that this Clock is the authority on timings of
 * each beat.
 */
public class Clock implements MicrosecondPositionProvider {
  private BeatsPerMinute beatsPerMinute;
  private long microsecondsPositionOfFirstBeat;
  private MicrosecondPositionProvider microsecondPositionProvider;

  public Clock(BeatsPerMinute beatsPerMinute) {
    this(beatsPerMinute, new DefaultMicrosecondPositionProvider());
  }

  private Clock(BeatsPerMinute beatsPerMinute,
                MicrosecondPositionProvider microsecondPositionProvider) {
    this(beatsPerMinute, microsecondPositionProvider, false);
  }

  private Clock(BeatsPerMinute beatsPerMinute,
                MicrosecondPositionProvider microsecondPositionProvider,
                boolean startAtNextSecond) {
    this.beatsPerMinute = beatsPerMinute;
    start(microsecondPositionProvider, startAtNextSecond);
  }

  /**
   * Start the clock.
   *
   * @param microsecondPositionProvider Micros second provider.
   * @param startAtNextSecond           Starting at next second can make debugging easier
   *                                    because microseconds position will start at a round
   *                                    number.
   */
  public void start(MicrosecondPositionProvider microsecondPositionProvider,
                    boolean startAtNextSecond) {
    if (startAtNextSecond) {
      this.microsecondsPositionOfFirstBeat = 1000000
          * (microsecondPositionProvider.getMicrosecondPosition() / 1000000);
    } else {
      this.microsecondPositionProvider = microsecondPositionProvider;
    }
  }

  public long getMicrosecondPosition() {
    return microsecondPositionProvider.getMicrosecondPosition();
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public double getBeat(long microseconds) {
    return (microseconds - microsecondsPositionOfFirstBeat)
        / beatsPerMinute.getMicroSecondsPerBeat();
  }

  public long getMicroSeconds(double beat) {
    return microsecondsPositionOfFirstBeat
        + (long) (beatsPerMinute.getMicroSecondsPerBeat() * beat);
  }

  public double getCurrentBeat() {
    return getBeat(microsecondPositionProvider.getMicrosecondPosition());
  }

}
