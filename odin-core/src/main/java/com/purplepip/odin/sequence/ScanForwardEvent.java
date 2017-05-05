package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;

/**
 * Event indicating that no note was found up to the given time that was scanned.  This
 * event signal can be used to allow a tock of a runtime sequence to be moved forward to this
 * point so that future scans for notes start from this point.
 */
public class ScanForwardEvent implements Event<Note> {
  private long time;

  public ScanForwardEvent(long time) {
    this.time = time;
  }

  @Override
  public Note getValue() {
    return null;
  }

  @Override
  public long getTime() {
    return time;
  }
}
