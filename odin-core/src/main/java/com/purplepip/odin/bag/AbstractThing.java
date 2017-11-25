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

package com.purplepip.odin.bag;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@ToString
@Data
public abstract class AbstractThing implements MutableThing {
  /*
   * Cheap ID generator for things.  Note that persistence implementation used for
   * the runtime has a more robust ID generation mechanism, however for the transient usage,
   * this cheap generator is good enough.
   */
  private static final AtomicLong LAST_PATTERN_ID = new AtomicLong();
  protected long id = LAST_PATTERN_ID.incrementAndGet();
  private String name;

  /**
   * ID auto generated.
   */
  public AbstractThing() {
    this(LAST_PATTERN_ID.incrementAndGet());
  }

  /**
   * ID taken from constructor.
   */
  public AbstractThing(long id) {
    setId(id);
  }

  @Override
  public long getId() {
    return id;
  }

  private void setId(long id) {
    this.id = id;
    /*
     * Auto generate name if one has not been provided.
     */
    if (name == null) {
      setName(String.valueOf(id));
    }
  }

  protected AbstractThing copy(AbstractThing copy) {
    copy.id = this.id;
    copy.name = this.name;
    return copy;
  }

  public AbstractThing name(String name) {
    this.name = name;
    return this;
  }
}
