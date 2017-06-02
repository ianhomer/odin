package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.TimeUnit;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistable tick.
 */
@Data
@NoArgsConstructor
@Entity(name = "Tick")
@Table(name = "Tick")
public class PersistableTick implements Tick {
  @Id
  @GeneratedValue
  private Long id;
  private TimeUnit timeUnit;
  private int numerator;
  private int denominator;

  public PersistableTick(Tick tick) {
    timeUnit = tick.getTimeUnit();
    numerator = tick.getNumerator();
    denominator = tick.getDenominator();
  }
}
