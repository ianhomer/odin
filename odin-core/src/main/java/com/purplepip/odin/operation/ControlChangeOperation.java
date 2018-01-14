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

public class ControlChangeOperation extends AbstractChannelOperation {
  private int control;
  private int value;

  /**
   * Create a control change operation.
   *
   * @param channel channel
   * @param control control function
   * @param value value to set
   */
  public ControlChangeOperation(int channel, int control, int value) {
    super(channel);
    this.control = control;
    this.value = value;
  }

  public int getControl() {
    return control;
  }

  public int getValue() {
    return value;
  }
}
