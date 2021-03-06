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

import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.performance.Performance;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable channel.
 */
@Data
@Entity(name = "Channel")
@Table(name = "Channel")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "performance")
@Slf4j
public class PersistableChannel extends PersistableThing implements Channel, PerformanceBound {
  private int number;
  private String programName;
  private int program;
  @ManyToOne(targetEntity = PersistablePerformance.class)
  @JoinColumn(name = "PERFORMANCE_ID", nullable = false)
  private Performance performance;

  @PreRemove
  public void removeFromPerformance() {
    performance.removeChannel(this);
  }
}
