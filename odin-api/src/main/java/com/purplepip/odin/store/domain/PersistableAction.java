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

import com.purplepip.odin.creation.action.MutableActionConfiguration;
import com.purplepip.odin.performance.Performance;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistable acton.
 */
@Data
@Entity(name = "Action")
@Table(name = "Action")
@ToString(exclude = "sequence", callSuper = true)
@EqualsAndHashCode(exclude = {"sequence"}, callSuper = true)
@Slf4j
public class PersistableAction extends PersistableTimeThing implements MutableActionConfiguration {
  @ManyToOne(targetEntity = PersistableSequence.class)
  @JoinColumn(name = "SEQUENCE_ID", nullable = false)
  private Performance sequence;

  @NotNull
  private String type;
}
