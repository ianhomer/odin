/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.Tick;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable pattern.
 */
@Data
@Entity(name = "Pattern")
@Table(name = "Pattern")
public class PersistablePattern extends PersistableSequence implements Pattern {
  private int bits;
  private int channel;
  @Column(name = "o")
  private long offset;
  private long length;
  @OneToOne(cascade = CascadeType.PERSIST, targetEntity = PersistableNote.class)
  private Note note;
  @OneToOne(cascade = CascadeType.PERSIST, targetEntity = PersistableTick.class)
  private Tick tick;
  private String flowName;
}
