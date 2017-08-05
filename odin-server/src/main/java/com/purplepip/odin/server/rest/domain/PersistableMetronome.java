/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Metronome;
import com.purplepip.odin.sequence.tick.Tick;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Persistable metronome.
 */
@Data
@Entity(name = "Metronome")
@Table(name = "Metronome")
@EqualsAndHashCode(callSuper = true)
public class PersistableMetronome extends AbstractPersistableSequence implements Metronome {
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(targetEntity = PersistableTick.class, cascade = CascadeType.ALL)
  @NotNull
  private Tick tick;
  @OneToOne(targetEntity = PersistableNote.class, cascade = CascadeType.ALL)
  private Note noteBarStart;
  @OneToOne(targetEntity = PersistableNote.class, cascade = CascadeType.ALL)
  private Note noteBarMid;
  @NotNull
  private String flowName;
}
