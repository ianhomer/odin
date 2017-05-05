package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.SequenceRuntime;

/**
 * SequenceRuntime driving a sequenced track.
 */
public class SeriesTrack {
  private SequenceRuntime<Note> sequenceRuntime;
  private int channel;

  public SeriesTrack(SequenceRuntime<Note> sequenceRuntime, int channel) {
    this.sequenceRuntime = sequenceRuntime;
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }

  public SequenceRuntime<Note> getSequenceRuntime() {
    return sequenceRuntime;
  }
}
