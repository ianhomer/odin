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

package com.purplepip.odin.events;

import com.purplepip.odin.common.OdinRuntimeException;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Event.
 */
@ToString
public class DefaultEvent<A> implements Event<A> {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultEvent.class);
  private A value;
  private long time;
  private long denominator;

  /**
   * Create a default event.
   *
   * @param value value for the event
   * @param time time of the event
   */
  public DefaultEvent(A value, long time) {
    this(value, time, 1);
  }

  /**
   * Create a default event.
   *
   * @param value value for the event
   * @param time time of the event
   * @param denominator denominator for the time of the event
   */
  public DefaultEvent(A value, long time, long denominator) {
    if (value == null) {
      throw new OdinRuntimeException("Cannot create an event with a null value");
    }
    LOG.trace("Creating new event : {} at time {}", value, time);
    this.value = value;
    this.time = time;
    this.denominator = denominator;
  }

  @Override
  public A getValue() {
    return value;
  }

  @Override
  public long getTime() {
    return time;
  }

  @Override
  public long getDenominator() {
    return denominator;
  }
}
