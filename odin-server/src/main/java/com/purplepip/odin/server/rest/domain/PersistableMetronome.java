package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.Tick;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable metronome.
 */
@Data
@Entity(name = "Metronome")
@Table(name = "Metronome")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PersistableMetronome extends PersistableSequence implements Metronome {
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(cascade = CascadeType.ALL)
  private PersistableTick tick;
  @OneToOne(cascade = CascadeType.ALL)
  private PersistableNote noteBarStart;
  @OneToOne(cascade = CascadeType.ALL)
  private PersistableNote noteMidBar;
  private String flowName;

  @Override
  public void setTick(Tick tick) {
    this.tick = (PersistableTick) tick;
  }

  @Override
  public void setNoteBarStart(Note note) {
    this.noteBarStart = (PersistableNote) note;
  }

  @Override
  public void setNoteMidBar(Note note) {
    this.noteMidBar = (PersistableNote) note;
  }
}
