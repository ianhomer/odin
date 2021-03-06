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

package com.purplepip.odin.operation;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Channel operation.
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractChannelOperation extends AbstractOperation
    implements ChannelOperation {
  private final int channel;

  protected AbstractChannelOperation(int channel) {
    this.channel = channel;
  }

  @Override
  public int getChannel() {
    return channel;
  }
}

