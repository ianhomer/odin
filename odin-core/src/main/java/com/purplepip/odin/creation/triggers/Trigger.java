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

package com.purplepip.odin.creation.triggers;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.operation.Operation;
import java.util.stream.Stream;

/**
 * Trigger rule.
 */
public interface Trigger extends TriggerConfiguration {
  boolean isTriggeredBy(Operation operation);

  /**
   * Inject a sequence configuration into this trigger so that the trigger can use this
   * sequence configuration for trigger logic.
   *
   * @param sequence sequence configuration to inject
   */
  default void inject(SequenceConfiguration sequence) {
    throw new OdinRuntimeException("Sequence can not be injected into "
        + this.getClass().getName());
  }

  /**
   * Get stream of sequence names that this trigger depends on.
   *
   * @return stream of sequence names that this trigger depends on
   */
  Stream<String> dependsOn();
}
