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

/**
 * Abstract operation.
 */
@EqualsAndHashCode
public abstract class AbstractOperation implements Operation {
  private Operation cause;
  private int causeDepth;

  @Override
  public Operation getCause() {
    return cause;
  }

  /**
   * Return the root cause.  This will return this operation if this option has no cause.
   *
   * @return root cause
   */
  @Override
  public Operation getRootCause() {
    if (hasCause()) {
      return cause.getRootCause();
    } else {
      return this;
    }
  }

  protected void setCause(Operation cause) {
    this.cause = cause;
    if (cause != null) {
      causeDepth = cause.getCauseDepth() + 1;
    }
  }

  public boolean hasCause() {
    return cause != null;
  }

  public int getCauseDepth() {
    return causeDepth;
  }
}
