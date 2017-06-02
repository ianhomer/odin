package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.Tick;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Persistable metronome.
 */
@Data
@Entity(name = "Metronome")
@Table(name = "Metronome")
public class PersistableMetronome implements Metronome {
  @Id
  @GeneratedValue
  private Long id;
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne
  private PersistableTick tick;
  @OneToOne
  private PersistableNote noteBarStart;
  @OneToOne
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
