package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime<S extends Sequence> extends MutableSequenceRuntime<S> {
  private SequenceEventProvider eventProvider;

  public DefaultSequenceRuntime(SequenceEventProvider eventProvider) {
    this.eventProvider = eventProvider;
  }

  @Override
  protected Event<Note> getNextEvent(Tock tock) {
    return eventProvider.getNextEvent(tock);
  }
}
