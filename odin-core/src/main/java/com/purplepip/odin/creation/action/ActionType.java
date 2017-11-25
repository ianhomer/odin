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

/**
 * Actions that can effect change on a sequence.  For example these might happen when a trigger
 * fires.
 */
public enum ActionType {
  /**
   * Enable the object attached to this trigger.
   */
  ENABLE,

  /*
   * Disable the object attached to this trigger.
   */
  DISABLE,

  /**
   * Reset the sequence.
   */
  RESET,

  /**
   * Start the object attached to this trigger.  Start is similar to enable, however will also set
   * the offset of the sequence to the next beat.
   */
  START,

  /**
   * Stop the object attached to this trigger.  Stop is currently implemented the same as disable.
   */
  STOP
}
