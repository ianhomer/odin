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

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable project.
 */
@Data
@Entity(name = "Project")
@Table(name = "Project")
public class PersistableProject implements Project {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  @OneToMany(targetEntity = AbstractPersistableSequence.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER)
  private Set<Sequence> sequences = new HashSet<>();
  @OneToMany(targetEntity = PersistableChannel.class, cascade = CascadeType.ALL,
      fetch = FetchType.EAGER)
  private Set<Channel> channels = new HashSet<>();
}
