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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.sequence.flow.RationalTypeConverter;
import com.purplepip.odin.sequence.flow.RealTypeConverter;
import com.purplepip.odin.specificity.AbstractSpecificThingFactory;
import java.util.ArrayList;
import java.util.List;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.typeconverter.TypeConverterManager;
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
    for (Class<? extends Trigger> clazz : classes) {
      register(clazz);
    }
  }

  static {
    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
  }

  /**
   * Create a copy of the given sequence configuration and cast to the required sequence type.
   *
   * @param triggerConfiguration sequence to use as a template for the one that is set
   */
  public Trigger newTrigger(TriggerConfiguration triggerConfiguration) {
    Class<? extends Trigger> expectedType = getClass(triggerConfiguration.getTriggerRule());
    return newTrigger(triggerConfiguration, expectedType);
  }

  /**
   * Create a copy of the trigger with the expected type.
   *
   * @param expectedType expected type of the returned sequence
   * @param original     original sequence to copy values from
   * @return trigger of expected type
   */
  @SuppressWarnings("unchecked")
  <S extends Trigger> S newTrigger(TriggerConfiguration original,
                                        Class<? extends S> expectedType) {
    S newTrigger;
    if (expectedType == null) {
      throw new OdinRuntimeException("Expected trigger type for " + original.getTriggerRule()
          + " is not set");
    } else {
      LOG.debug("Creating new trigger with class {}", expectedType.getName());
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply take a copy
         */
        newTrigger = (S) expectedType.cast(original).copy();
        LOG.debug("Starting flow with trigger copy {}", newTrigger);
      } else {
        LOG.debug("Creating new instance of {}", expectedType);
        try {
          newTrigger = expectedType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + expectedType, e);
        }
        final MutableTriggerConfiguration mutableTrigger =
            (MutableTriggerConfiguration) newTrigger;
        LOG.debug("Copying original object properties to copy");
        BeanCopy.from(original).to(newTrigger).copy();
        LOG.debug("Copying original properties map to copy");
        original.getPropertyNames()
            .forEach(name -> {
              mutableTrigger.setProperty(name, original.getProperty(name));
              try {
                BeanUtil.declared.setProperty(mutableTrigger, name, original.getProperty(name));
              } catch (BeanException e) {
                LOG.debug("Whilst creating sequence {} (full stack)", e);
                LOG.warn("Whilst creating sequence {}", e.getMessage());
              }
            });
        mutableTrigger.afterPropertiesSet();
        LOG.debug("Creating trigger with typed copy {} ; class = {}", mutableTrigger,
            mutableTrigger.getClass().getName());
      }
    }
    return newTrigger;
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
      trigger.setTriggerRule(name);
      newTrigger(trigger);
    });
  }
}
