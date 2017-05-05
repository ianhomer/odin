package com.purplepip.odin.music;

import com.purplepip.odin.sequence.SequenceConfiguration;
import com.purplepip.odin.sequence.Tick;

/**
 * Metronome configuration.
 */
public class MetronomeConfiguration implements SequenceConfiguration {
  private Note noteBarStart;
  private Note noteMidBar;
  private long length = -1;

  public MetronomeConfiguration() {
    noteBarStart = new DefaultNote();
    noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
  }

  public long getLength() {
    return length;
  }

  public Note getNoteBarStart() {
    return noteBarStart;
  }

  public Note getNoteMidBar() {
    return noteMidBar;
  }

  @Override
  public Tick getTick() {
    return Tick.HALF;
  }

}
