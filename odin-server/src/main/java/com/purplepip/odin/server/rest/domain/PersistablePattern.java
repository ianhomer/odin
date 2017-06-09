package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
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
 * Persistable pattern.
 */
@Data
@Entity(name = "Pattern")
@Table(name = "Pattern")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersistablePattern extends PersistableSequence implements Pattern {
  private int bits;
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(cascade = CascadeType.PERSIST)
  private PersistableNote note;
  @OneToOne(cascade = CascadeType.PERSIST)
  private PersistableTick tick;
  private String flowName;

  @Override
  public void setTick(Tick tick) {
    this.tick = (PersistableTick) tick;
  }

  @Override
  public void setNote(Note note) {
    this.note = (PersistableNote) note;
  }
}
