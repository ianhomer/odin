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
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TimeThing;
import com.purplepip.odin.clock.tick.TimeUnit;
import com.purplepip.odin.math.Rational;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = "TimeThing")
@EqualsAndHashCode(exclude = {"offset", "length", "tick"}, callSuper = true)
@ToString()
@Slf4j
public class PersistableTimeThing extends PersistablePropertiesThing implements TimeThing {
  private boolean enabled;
  @Column(name = "o")

  @Min(0)
  private long offsetNumerator;
  @Min(1)
  private long offsetDenominator = 1;

  @Transient
  @JsonIgnore
  private Rational offset;

  @Min(-1)
  private long lengthNumerator;
  @Min(1)
  private long lengthDenominator = 1;

  @Transient
  @JsonIgnore
  private Rational length;

  @OneToOne(targetEntity = PersistableTick.class, cascade = CascadeType.ALL, orphanRemoval = true)
  @NotNull
  private Tick tick;

  /**
   * Set default values.
   */
  private void setTimeThingDefaults() {
    if (tick == null) {
      PersistableTick newTick = new PersistableTick();
      newTick.setTimeUnit(TimeUnit.BEAT);
      newTick.setNumerator(1);
      newTick.setDenominator(1);
      tick = newTick;
    }
    if (offset != null) {
      offsetNumerator = offset.getNumerator();
      offsetDenominator = offset.getDenominator();
    }
    if (length != null) {
      lengthNumerator = length.getNumerator();
      lengthDenominator = length.getDenominator();
    }
  }

  @PrePersist
  public void preTimeThingPersist() {
    setTimeThingDefaults();
  }

  @Override
  @JsonIgnore
  public Rational getLength() {
    return length;
  }

  @Override
  @JsonIgnore
  public Rational getOffset() {
    return offset;
  }

  @PostPersist
  @PostLoad
  @PostUpdate
  protected void afterTimeThingLoad() {
    initialiseTimeThing();
  }

  private void initialiseTimeThing() {
    LOG.debug("Initialising time thing : {}", this);
    offset = Rational.valueOf(offsetNumerator, offsetDenominator);
    length = Rational.valueOf(lengthNumerator, lengthDenominator);
  }
}
