package com.purplepip.odin.server;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.server.rest.PersistableSequenceBuilder;
import com.purplepip.odin.server.rest.domain.PersistablePattern;
import com.purplepip.odin.server.rest.repositories.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database Loader.
 */
@Component
public class DatabaseLoader implements CommandLineRunner {
  @Autowired
  private OdinSequencer sequencer;

  private final PatternRepository repository;

  @Autowired
  public DatabaseLoader(PatternRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... strings) throws Exception {
    new PersistableSequenceBuilder(sequencer.getProject())
        .addMetronome()
        .addPattern(Ticks.BEAT, 2)
        .withChannel(9).withNote(42).addPattern(Ticks.QUARTER, 61435)
        .addPattern(Ticks.EIGHTH, 127)
        .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);

    for (Sequence<Note> sequence : sequencer.getProject().getSequences()) {
      if (sequence instanceof Pattern) {
        PersistablePattern persistablePattern = (PersistablePattern) sequence;
        if (persistablePattern.getNote() ==  null) {
          throw new OdinRuntimeException("Note must not be null");
        }
        if (persistablePattern.getTick() ==  null) {
          throw new OdinRuntimeException("Tick must not be null");
        }
        this.repository.save((PersistablePattern) sequence);
      }
    }

  }
}
