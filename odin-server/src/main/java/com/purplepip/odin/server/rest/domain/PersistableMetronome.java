package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.Tick;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable metronome.
 */
@Data
@Entity(name = "Metronome")
@Table(name = "Metronome")
public class PersistableMetronome extends PersistableSequence implements Metronome {
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(cascade = CascadeType.ALL, targetEntity = PersistableTick.class)
  private Tick tick;
  @OneToOne(cascade = CascadeType.ALL, targetEntity = PersistableNote.class)
  private Note noteBarStart;
  @OneToOne(cascade = CascadeType.ALL, targetEntity = PersistableNote.class)
  private Note noteMidBar;
  private String flowName;
}
