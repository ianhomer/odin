package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.flow.PatternFlow;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.Tick;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Persistable pattern.
 */
@Data
@Entity(name = "Pattern")
@Table(name = "Pattern")
public class PersistablePattern implements Pattern {
  @Id
  @GeneratedValue
  private Long id;
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
