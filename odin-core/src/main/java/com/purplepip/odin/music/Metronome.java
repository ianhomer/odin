package com.purplepip.odin.music;

import com.purplepip.odin.sequence.AbstractSequence;
import com.purplepip.odin.sequence.DefaultEvent;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MutableTock;
import com.purplepip.odin.sequence.SequenceEventProvider;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Tock;

/**
 * MetronomeRuntime configuration.
 */
public class Metronome extends AbstractSequence {
  private Note noteBarStart;
  private Note noteMidBar;

  /**
   * Create a metronome.
   */
  public Metronome() {
    noteBarStart = new DefaultNote();
    noteMidBar = new DefaultNote(64, noteBarStart.getVelocity() / 2);
    setTick(Tick.HALF);
  }

  /**
   * Get note for the start of the bar.
   *
   * @return note
   */
  public Note getNoteBarStart() {
    return noteBarStart;
  }

  /**
   * Get note for mid bar.
   *
   * @return note
   */
  public Note getNoteMidBar() {
    return noteMidBar;
  }

  @Override
  public SequenceEventProvider createEventProvider(final MeasureProvider measureProvider) {

    return new SequenceEventProvider() {
      @Override
      public Event<Note> getNextEvent(Tock tock) {
        /*
         * Create local and temporary mutable tock for this function execution.
         */
        MutableTock mutableTock = new MutableTock(tock);
        mutableTock.increment(2);
        Note note;
        if (measureProvider.getTickPositionInThisMeasure(mutableTock) == 0) {
          note = Metronome.this.getNoteBarStart();
        } else {
          note = Metronome.this.getNoteMidBar();
        }
        return new DefaultEvent<>(note, mutableTock.getCount());
      }
    };
  }
}
