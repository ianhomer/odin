package com.purplepip.odin.music.logic;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.logic.AbstractLogic;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Metronome logic.
 */
public class MetronomeLogic extends AbstractLogic<Metronome, Note> {
  public MetronomeLogic(Metronome metronome) {
    setSequence(metronome);
  }

  @Override
  public Event<Note> getNextEvent(Tock tock, MeasureProvider measureProvider) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    MutableTock mutableTock = new MutableTock(tock);
    mutableTock.increment(2);
    Note note;
    if (measureProvider.getTickPositionInThisMeasure(mutableTock) == 0) {
      note = getSequence().getNoteBarStart();
    } else {
      note = getSequence().getNoteMidBar();
    }
    return new DefaultEvent<>(note, mutableTock.getCount());
  }
}
