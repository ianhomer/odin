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

package com.purplepip.odin.creation.channel;

import com.purplepip.odin.bag.AbstractThing;
import java.util.concurrent.atomic.AtomicLong;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default channel implementation.
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class DefaultChannel extends AbstractThing implements Channel {
  /*
   * Cheap ID generator for default channels.  Note that persistence implementation used for
   * the runtime has a more robust ID generation mechanism, however for the transient usage,
   * this cheap generator is good enough.
   */
  private static final AtomicLong LAST_PATTERN_ID = new AtomicLong();

  /*
   * ID auto generated.
   */
  protected final long defaultChannelId = LAST_PATTERN_ID.incrementAndGet();

  private int number;
  private String programName;
  private int program;

  public DefaultChannel() {
  }

  public DefaultChannel(int number) {
    this();
    setNumber(number);
  }

  public static DefaultChannel newChannel(int number) {
    return new DefaultChannel(number);
  }

  /**
   * Create a new channel.
   *
   * @param name program name AND name of this entity
   * @param number channel number
   * @return new channel
   */
  public static DefaultChannel newChannel(String name, int number) {
    DefaultChannel channel = newChannel(number);
    channel.setName(name);
    channel.setProgramName(name);
    return channel;
  }

  @Override
  public long getId() {
    return defaultChannelId;
  }

  @Override
  public int getNumber() {
    return number;
  }

  @Override
  public void setNumber(int number) {
    this.number = number;
  }

  @Override
  public String getProgramName() {
    return programName;
  }

  @Override
  public void setProgramName(String programName) {
    this.programName = programName;
  }

  @Override
  public int getProgram() {
    return program;
  }

  @Override
  public void setProgram(int program) {
    this.program = program;
  }

  public DefaultChannel number(int number) {
    this.number = number;
    return this;
  }

  public DefaultChannel programName(String programName) {
    this.programName = programName;
    return this;
  }
}
