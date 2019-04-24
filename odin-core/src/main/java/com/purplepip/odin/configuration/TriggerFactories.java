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

package com.purplepip.odin.configuration;

import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.creation.triggers.PatternNoteTrigger;
import com.purplepip.odin.creation.triggers.SequenceStartTrigger;
import com.purplepip.odin.creation.triggers.Trigger;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import java.util.ArrayList;
import java.util.List;

public final class TriggerFactories {
  /**
   * Create a new trigger factory.
   *
   * @return a new trigger factory
   */
  public static TriggerFactory newTriggerFactory() {
    /*
     * Coded registration of known sequences.  In the future we may design a plugin architecture,
     * but for now it is kept tight by only allowing registered classes.
     */
    List<Class<? extends Trigger>> classes = new ArrayList<>();
    classes.add(NoteTrigger.class);
    classes.add(PatternNoteTrigger.class);
    classes.add(SequenceStartTrigger.class);
    return new TriggerFactory(classes);
  }

  private TriggerFactories() {
  }
}
