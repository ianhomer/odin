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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@ToString
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
    id = LAST_PATTERN_ID.incrementAndGet();
  }

  /**
   * Name taken from constructor.
   */
  public AbstractThing(String name) {
    this.name = name;
  }

  /**
   * ID taken from constructor.
   */
  public AbstractThing(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
  }

  protected void setId(long id) {
    this.id = id;
    /*
     * Auto generate name if one has not been provided.
     */
    if (name == null) {
      setName(String.valueOf(id));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
