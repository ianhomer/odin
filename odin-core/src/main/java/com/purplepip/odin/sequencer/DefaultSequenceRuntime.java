package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.flow.Flow;

/**
 * Default sequence runtime.
 */
public class DefaultSequenceRuntime extends MutableSequenceRuntime<Sequence, Note> {
  private Flow<Sequence<Note>, Note> flow;

  public DefaultSequenceRuntime(Flow<Sequence<Note>, Note> flow) {
    this.flow = flow;
  }

  @Override
  protected Event<Note> getNextEvent(Tock tock) {
    return flow.getNextEvent(tock, getMeasureProvider());
  }
}
