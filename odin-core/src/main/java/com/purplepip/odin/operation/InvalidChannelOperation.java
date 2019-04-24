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

/**
 * A channel operation may be transformed into an invalid channel operation if the system
 * determines that a given channel operation is invalid.
 */
public class InvalidChannelOperation extends AbstractChannelOperation {
  private final String message;

  /**
   * Create an invalid channel operation.
   *
   * @param cause original channel operation that caused this.
   * @param message message providing reason for invalid operation.
   */
  public InvalidChannelOperation(ChannelOperation cause, String message) {
    super(cause.getChannel());
    setCause(cause);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public ChannelOperation getCause() {
    return (ChannelOperation) super.getCause();
  }
}
