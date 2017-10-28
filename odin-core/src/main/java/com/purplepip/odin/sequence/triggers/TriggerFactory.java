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

package com.purplepip.odin.sequence.triggers;

import com.purplepip.odin.specificity.AbstractSpecificThingFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Create trigger rules.
 */
// TODO : Reuse with SequenceFactory
@Slf4j
public class TriggerFactory extends AbstractSpecificThingFactory<Trigger> {
  /**
   * Create the note sequence factory.
   *
   * @return a new note sequence factory
   */
  public static TriggerFactory createTriggerFactory() {
    /*
     * Coded registration of known sequences.  In the future we may design a plugin architecture,
     * but for now it is kept tight by only allowing registered classes.
     */
    List<Class<? extends Trigger>> classes = new ArrayList<>();
    classes.add(NoteTrigger.class);
    TriggerFactory triggerFactory = new TriggerFactory(classes);
    triggerFactory.warmUp();
    return triggerFactory;
  }

  /**
   * Create a new trigger factory.
   *
   * @param classes trigger classes to initialise with
   */
  public TriggerFactory(List<Class<? extends Trigger>> classes) {
    super(classes);
  }

  /**
   * Create a copy of the given sequence configuration and cast to the required sequence type.
   *
   * @param triggerConfiguration sequence to use as a template for the one that is set
   */
  public Trigger newTrigger(TriggerConfiguration triggerConfiguration) {
    Class<? extends Trigger> expectedType = getClass(triggerConfiguration.getType());
    return newInstance(triggerConfiguration, expectedType);
  }

  /**
   * For test cases where timing is important it may be necessary to warm the factory up
   * so the first time it is used performance does not cause inconsistencies.  This warm up
   * time is pretty small (around 20ms on a dev machine), but enough to throw a sequencer
   * test that is expecting sequence to start immediately.
   */
  private void warmUp() {
    getNames().forEach(name -> {
      MutableTriggerConfiguration trigger = new GenericTrigger();
      trigger.setType(name);
      newTrigger(trigger);
    });
  }
}
