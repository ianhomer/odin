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

package com.purplepip.odin.creation.action;

import com.purplepip.odin.operation.AbstractOperation;
import com.purplepip.odin.operation.Operation;
import lombok.ToString;

/**
 * Operation fired to start an operation.
 */
@ToString
public class ActionOperation extends AbstractOperation {
  private String trackName;
  private Action action;

  /**
   * Create an action operation.
   *
   * @param action action
   * @param trackName track name action is applied to
   * @param cause operation that caused this action
   */
  public ActionOperation(Action action, String trackName, Operation cause) {
    setCause(cause);
    this.trackName = trackName;
    this.action = action;
  }

  public Action getAction() {
    return action;
  }

  public String getTrackName() {
    return trackName;
  }
}
