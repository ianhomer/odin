package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.sequencer.ProjectBuilder;
import com.purplepip.odin.server.rest.domain.PersistableChannel;
import com.purplepip.odin.server.rest.domain.PersistableMetronome;
import com.purplepip.odin.server.rest.domain.PersistableNote;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import com.purplepip.odin.server.rest.domain.PersistableTick;

/**
 * Sequence builder that updates the project with persistable domain objects.
 */
public class PersistableProjectBuilder extends ProjectBuilder {
  public PersistableProjectBuilder(ProjectContainer projectContainer) {
    super(projectContainer);
  }

  @Override
  protected Metronome createMetronome() {
    return new PersistableMetronome();
  }

  /**
   * Create a persistable note.
   *
   * @return note
   */
  @Override
  protected Note createNote(int note, int velocity, long duration) {
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
    return new PersistablePattern();
  }

  @Override
  protected Channel createChannel() {
    return new PersistableChannel();
  }

  @Override
  protected Tick createTick(Tick tick) {
    return new PersistableTick(tick);
  }
}
