package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.sequence.Sequence;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Persistable sequence.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PersistableSequence implements Sequence {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  public Long id;
}
