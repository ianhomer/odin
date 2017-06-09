package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.sequence.Sequence;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

/**
 * Persistable sequence.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PersistableSequence implements Sequence {
  @Id
  @GeneratedValue
  private Long id;
}
