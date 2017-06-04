package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.TimeUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  /**
   * Create a persistable tick.
   *
   * @param tick tick to copy parameters from
   */
  public PersistableTick(Tick tick) {
    timeUnit = tick.getTimeUnit();
    numerator = tick.getNumerator();
    denominator = tick.getDenominator();
  }
}
