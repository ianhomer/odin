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

import com.purplepip.odin.specificity.Name;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Start the object attached to this trigger.  Start is similar to enable, however will also set
 * the offset of the sequence to the next beat.
 */
@Slf4j
@Name("start")
@ToString
public class StartAction extends ActionPlugin {
  @Override
  public void execute(ActionContext context) {
    context.getTrack().start();
  }

  /**
   * Create a copy of this action.
   *
   * @return copy
   */
  @Override
  public StartAction copy() {
    return copy(new StartAction());
  }
}
