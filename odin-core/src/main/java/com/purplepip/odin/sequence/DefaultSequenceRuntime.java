package com.purplepip.odin.sequence;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.logic.Logic;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime<S extends Sequence> extends MutableSequenceRuntime<S> {
  private Logic eventProvider;

  public DefaultSequenceRuntime(Logic eventProvider) {
    this.eventProvider = eventProvider;
  }

  @Override
  protected Event<Note> getNextEvent(Tock tock) {
    return eventProvider.getNextEvent(tock);
  }
}
