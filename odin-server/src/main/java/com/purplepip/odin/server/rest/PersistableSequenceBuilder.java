package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.DefaultNote;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.MetronomeFlow;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequencer.SequenceBuilder;
import com.purplepip.odin.server.rest.domain.PersistableMetronome;
import com.purplepip.odin.server.rest.domain.PersistableNote;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import com.purplepip.odin.server.rest.domain.PersistableTick;

/**
 * Sequence builder that updates the project with persistable domain objects.
 */
public class PersistableSequenceBuilder extends SequenceBuilder {
  public PersistableSequenceBuilder(Project project) {
    super(project);
  }

  @Override
  protected Metronome createMetronome() {
    PersistableMetronome metronome = new PersistableMetronome();
    metronome.setNoteBarStart(new PersistableNote());
    metronome.setNoteMidBar((PersistableNote) createNote(64,
        metronome.getNoteBarStart().getVelocity() / 2, 1));
    metronome.setTick(createTick(Ticks.HALF));
    metronome.setFlowName(MetronomeFlow.class.getName());
    return metronome;
  }

  /**
   * Create a persistable note.
   *
   * @return note
   */
  @Override
  protected Note createNote(int note, int velocity, int duration) {
    PersistableNote persistableNote = new PersistableNote();
    persistableNote.setDuration(duration);
    persistableNote.setVelocity(velocity);
    persistableNote.setNote(note);
    return persistableNote;
  }

  /**
   * Create a persistable pattern.
   *
   * @return pattern
   */
  @Override
  protected Pattern createPattern() {
    PersistablePattern pattern = new PersistablePattern();
    pattern.setFlowName(PatternFlow.class.getName());
    return pattern;
  }

  @Override
  protected Tick createTick(Tick tick) {
    return new PersistableTick(tick);
  }
}
