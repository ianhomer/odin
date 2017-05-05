package com.purplepip.odin.music.logic;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.logic.Logic;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.Tock;

/**
 * Metronome logic.
 */
public class MetronomeLogic implements Logic {
  private Metronome metronome;
  private MeasureProvider measureProvider;

  public MetronomeLogic(Metronome metronome, MeasureProvider measureProvider) {
    this.metronome = metronome;
    this.measureProvider = measureProvider;
  }

  @Override
  public Event<Note> getNextEvent(Tock tock) {
        /*
         * Create local and temporary mutable tock for this function execution.
         */
    MutableTock mutableTock = new MutableTock(tock);
    mutableTock.increment(2);
    Note note;
    if (measureProvider.getTickPositionInThisMeasure(mutableTock) == 0) {
      note = metronome.getNoteBarStart();
    } else {
      note = metronome.getNoteMidBar();
    }
    return new DefaultEvent<>(note, mutableTock.getCount());
  }
}
