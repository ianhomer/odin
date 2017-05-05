package com.purplepip.odin.music;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tick;

/**
 * MetronomeRuntime configuration.
 */
public class Metronome implements Sequence {
  private Note noteBarStart;
  private Note noteMidBar;
  private long length = -1;

  public Metronome() {
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
