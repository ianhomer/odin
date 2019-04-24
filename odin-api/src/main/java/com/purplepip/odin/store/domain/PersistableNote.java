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
import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.music.notes.Note;
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

/**
 * Persistable note.
 */
@Data
@Entity(name = "Note")
@Table(name = "Note")
@EqualsAndHashCode(of = "id")
public class PersistableNote implements Note {
  @Id
  @GeneratedValue
  @JsonIgnore
  private long id;
  private int velocity;
  private int number;
  @Min(1)
  private long numerator;
  @Min(1)
  private long denominator;

  @Transient
  @JsonIgnore
  private Rational duration;

  @Override
  @JsonIgnore
  public Rational getDuration() {
    return duration;
  }

  @PostPersist
  @PostLoad
  @PostUpdate
  public void afterLoad() {
    initialise();
  }

  private void initialise() {
    duration = Rationals.valueOf(numerator, denominator);
  }
}
