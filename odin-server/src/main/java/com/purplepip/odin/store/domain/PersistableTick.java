/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.sequence.tick.TimeUnit;
import com.purplepip.odin.sequence.tick.Tick;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Persistable tick.
 */
@Data
@NoArgsConstructor
@Entity(name = "Tick")
@Table(name = "Tick")
@EqualsAndHashCode(of = "id")
public class PersistableTick implements Tick {
  @JsonIgnore
  @Id
  @GeneratedValue
  private long id;
  private TimeUnit timeUnit;
  @Min(1)
  private long numerator;
  @Min(1)
  private long denominator;

  @Transient
  @JsonIgnore
  private Rational factor;

  /**
   * Create a persistable tick.
   *
   * @param tick tick to copy parameters from
   */
  public PersistableTick(Tick tick) {
    timeUnit = tick.getTimeUnit();
    numerator = tick.getFactor().getNumerator();
    denominator = tick.getFactor().getDenominator();
    initialise();
  }

  @Override
  @JsonIgnore
  public Rational getFactor() {
    return factor;
  }

  @PostPersist
  @PostLoad
  @PostUpdate
  protected void afterLoad() {
    initialise();
  }

  private void initialise() {
    factor = new Rational(numerator, denominator);
  }
}
