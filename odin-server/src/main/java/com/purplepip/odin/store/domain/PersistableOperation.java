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

package com.purplepip.odin.store.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Persistable operation used currently for auditing purposes, but in the future it could be
 * used for replay.
 */
@Data
@Entity(name = "Operation")
@Table(name = "Operation")
@EqualsAndHashCode(of = "id")
public class PersistableOperation {
  @Id
  @GeneratedValue
  private long id;

  @Temporal(TemporalType.DATE)
  private Date dateCreated;

  private String message;
  private long time;
  private int channel;
  private int number;
  private int velocity;

  @PrePersist
  protected void onPrePersist() {
    dateCreated = new Date();
  }
}
