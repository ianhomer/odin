package com.purplepip.odin.sequence;

/**
 * Event indicating that no value was found up to the given time that was scanned.  This
 * event signal can be used to allow a tock of a runtime sequence to be moved forward to this
 * point so that future scans for values start from this point.
 */
public class ScanForwardEvent<A> implements Event<A> {
  private long time;

  public ScanForwardEvent(long time) {
    this.time = time;
  }

  @Override
  public A getValue() {
    return null;
  }

  @Override
  public long getTime() {
    return time;
  }
}
