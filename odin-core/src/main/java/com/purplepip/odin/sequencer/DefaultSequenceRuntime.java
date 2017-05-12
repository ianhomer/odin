package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.logic.Logic;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime extends MutableSequenceRuntime<Sequence, Note> {
  private Logic<Sequence<Note>, Note> logic;

  public DefaultSequenceRuntime(Logic<Sequence<Note>, Note> logic) {
    this.logic = logic;
  }

  @Override
  protected Event<Note> getNextEvent(Tock tock) {
    return logic.getNextEvent(tock, getMeasureProvider());
  }
}
