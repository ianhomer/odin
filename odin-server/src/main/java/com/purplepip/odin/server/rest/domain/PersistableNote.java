package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistable note.
 */
@Data
@Entity(name = "Note")
@Table(name = "Note")
public class PersistableNote implements Note {
  @Id
  @GeneratedValue
  private Long id;
  private int velocity;
  private int note;
  private int number;
  private long duration;
}
