package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Sequence;

/**
 * Sequence driving a sequenced track.
 */
public class SeriesTrack {
  private Sequence<Note> sequence;
  private int channel;

  public SeriesTrack(Sequence<Note> sequence, int channel) {
    this.sequence = sequence;
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }

  public Sequence<Note> getSequence() {
    return sequence;
  }
}
