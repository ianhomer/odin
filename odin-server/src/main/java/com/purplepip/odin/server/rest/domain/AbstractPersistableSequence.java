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

import com.purplepip.odin.sequence.Sequence;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Persistable sequence.
 */
@Entity(name = "Sequence")
@Table(name = "Sequence")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractPersistableSequence implements Sequence {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  public Long id;
}
