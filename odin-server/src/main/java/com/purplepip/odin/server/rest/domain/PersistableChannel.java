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
import com.purplepip.odin.sequencer.Channel;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Persistable channel.
 */
@Data
@Entity(name = "Channel")
@Table(name = "Channel")
@EqualsAndHashCode(exclude = "project")
@ToString(exclude = "project")
public class PersistableChannel implements Channel {
  @Id
  @GeneratedValue
  private Long id;
  private int number;
  private String programName;
  private int program;
  @ManyToOne(targetEntity = PersistableProject.class)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private Project project;

  @PreRemove
  public void removeFromProject() {
    project.removeChannel(this);
  }
}
