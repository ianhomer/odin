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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.common.Stringy;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract thing.
 */
/*
 * TODO : Define precisely how we define equality AND how IDs are used to update entities
 * Note that two objects are equal if they are the same type and have all the
 * same properties are equal, without the ID being taken into account.   The ID however is used
 * for persistence model and updating things in AbstractPluggableAspects.  These two definitions
 * of equality seem a little inconsistent and may cause issues in the future.  For example equality
 * is used in set storage, so we need to think what duplicate means.  Maybe equality
 * should be based only on ID and we should have separate API to check whether data is the same.
 */
@Slf4j
@EqualsAndHashCode(of = "name")
@Data
public abstract class AbstractThing implements MutableThing {
  /*
   * Cheap ID generator for things.  Note that persistence implementation used for
   * the runtime has a more robust ID generation mechanism, however for the transient usage,
   * this cheap generator is good enough.
   */
  private static final AtomicLong LAST_THING_ID = new AtomicLong();
  private long id;
  private String name;

  @JsonIgnore
  private transient int initialisationCount = 0;

  /**
   * ID auto generated.
   */
  public AbstractThing() {
    this(LAST_THING_ID.incrementAndGet());
  }

  /**
   * ID taken from constructor.
   */
  public AbstractThing(long id) {
    this.id = id;
    /*
     * By default set the thing name to id.  Note that name must not be null.
     */
    setName(String.valueOf(id));
  }

  @Override
  public long getId() {
    return id;
  }

  protected MutableThing copy(MutableThing copy) {
    copy.setId(this.id);
    copy.setName(this.name);
    return copy;
  }

  /**
   * Set name.
   *
   * @param name name
   */
  public void setName(String name) {
    if (name == null) {
      throw new OdinRuntimeException("Name must not be null");
    }
    this.name = name;
  }

  public AbstractThing name(String name) {
    setName(name);
    return this;
  }

  @JsonIgnore
  @Override
  public void initialise() {
    initialisationCount++;
    LOG.debug("{} : initialising : count = {}", getName(), initialisationCount);
  }

  /**
   * To string.
   *
   * @return to string
   */
  public String toString() {
    return Stringy.of(AbstractThing.class, this)
        .add("name", getName())
        .build();
  }
}
