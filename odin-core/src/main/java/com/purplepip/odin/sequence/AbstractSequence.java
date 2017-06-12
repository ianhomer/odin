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

package com.purplepip.odin.sequence;

/**
 * Abstract sequence.
 */
public abstract class AbstractSequence implements MutableSequence {
  private Tick tick;
  private long length = -1;
  private long offset;
  private int channel;
  private String flowName;

  @Override
  public void setTick(Tick tick) {
    this.tick = tick;
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  /**
   * Set the length of the series in ticks.
   *
   * @param length length of series in ticks
   */
  @Override
  public void setLength(long length) {
    this.length = length;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public void setOffset(long offset) {
    this.offset = offset;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public void setChannel(int channel) {
    this.channel = channel;
  }

  @Override
  public int getChannel() {
    return channel;
  }

  @Override
  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  @Override
  public String getFlowName() {
    return flowName;
  }
}
