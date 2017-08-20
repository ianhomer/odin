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

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = "Thing")
@EqualsAndHashCode(of = "id")
@ToString()
public class PersistableThing {
  @Version
  @JsonIgnore
  private Long version;

  @Id
  @GeneratedValue
  private long id;

  @NotNull
  private String name;

  /**
   * Generate string of just the core thing properties.
   *
   * @return string
   */
  public String toThingString() {
    return "PersistableThing(version=" + this.getVersion()
        + ", id=" + this.getId()
        + ", name=" + this.getName() + ")";
  }
}
