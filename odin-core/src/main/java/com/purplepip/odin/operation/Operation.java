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
 * Operation that can be fired by sequencer.
 */
public interface Operation {
  /**
   * Get the operation (if any that caused this operation).  Since operations can trigger other
   * operations it is important that we don't end up with a recursive loop.   The operation
   * processor can put safety nets based on detecting recursion or reaching maximum depth of
   * causation.
   *
   * @return operation that caused this operation.
   */
  Operation getCause();

  boolean hasCause();

  int getCauseDepth();
}
