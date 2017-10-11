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

package com.purplepip.odin.sequence.triggers.rule;

import com.purplepip.odin.sequence.triggers.NoteTrigger;

/**
 * Create trigger rules.
 */
public class TriggerRuleFactory {
  /**
   * Create trigger rule.
   *
   * @param trigger trigger
   * @return trigger rule
   */
  public NoteTriggerRule createRule(NoteTrigger trigger) {
    /*
     * TODO : Implement generic logic, but before that is done, review, simplify and reuse
     * how it is done for sequences.  Perhaps this is the time to define the plugin interface
     * and how that gets initialised.
     */
    return new NoteTriggerRule(trigger);
  }
}
