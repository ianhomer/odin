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

package com.purplepip.odin.creation.sequence;

import com.purplepip.odin.bag.ThingName;
import com.purplepip.odin.clock.tick.AbstractTimeThing;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.action.ActionConfiguration;
import com.purplepip.odin.creation.action.ListAction;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.specificity.NameValue;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract sequence.
 */
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class GenericSequence extends AbstractTimeThing implements MutableSequenceConfiguration {
  private int channel;
  private final String type;
  private List<String> layers = new ArrayList<>();
  private Map<String, ActionConfiguration> triggers = new HashMap<>();

  /**
   * Create new generic sequence.
   */
  GenericSequence(Class<? extends ThingConfiguration> clazz) {
    super();
    type = new NameValue(clazz).get();
  }

  public GenericSequence(String type) {
    super();
    this.type = type;
  }

  public GenericSequence(String type, long id) {
    super(id);
    this.type = type;
  }

  @Override
  public List<String> getLayers() {
    return layers;
  }

  @Override
  public void addLayer(String layer) {
    LOG.debug("Adding layer to {} : {}", getName(), layer);
    layers.add(layer);
  }

  /**
   * Set layer names.
   *
   * @param layerNames layer names
   * @return this
   */
  public GenericSequence layer(ThingName... layerNames) {
    for (ThingName layerName : layerNames) {
      addLayer(layerName.getValue());
    }
    return this;
  }

  /**
   * Set layer names.
   *
   * @param layerNames layer names
   * @return this
   */
  public GenericSequence layer(String... layerNames) {
    for (String layerName : layerNames) {
      addLayer(layerName);
    }
    return this;
  }

  @Override
  public void removeLayer(String layer) {
    layers.remove(layer);
  }

  @Override
  public boolean isEmpty() {
    return layers.isEmpty();
  }

  @Override
  public Map<String, ActionConfiguration> getTriggers() {
    return triggers;
  }

  @Override
  public void addTrigger(String trigger, ActionConfiguration action) {
    LOG.debug("Adding trigger : {} when {} ", action, trigger);
    triggers.put(trigger, action);
  }

  /**
   * Set which action (or actions) are fired by the given trigger name.
   *
   * @param trigger trigger name to respond to
   * @param actions actions that should be fired
   * @return this
   */
  // TODO : Generic sequence should not be aware of Action plugin, we need to create alternative
  // way.
  public GenericSequence trigger(String trigger, ActionConfiguration... actions) {
    if (actions.length == 1) {
      addTrigger(trigger, actions[0]);
    } else {
      ListAction.asActionConfigurationMap(trigger, actions).forEach(this::addTrigger);
    }
    return this;
  }

  @Override
  public void removeTrigger(String trigger) {
    triggers.remove(trigger);
  }

  protected MutableSequenceConfiguration copy(MutableSequenceConfiguration copy) {
    copy.setChannel(this.getChannel());
    this.getLayers().forEach(copy::addLayer);
    this.getTriggers().forEach(copy::addTrigger);
    super.copy(copy);
    return copy;
  }

  public GenericSequence channel(int channel) {
    this.channel = channel;
    return this;
  }

  @Override
  public GenericSequence name(String name) {
    super.name(name);
    return this;
  }

  @Override
  public GenericSequence enabled(boolean enabled) {
    super.enabled(enabled);
    return this;
  }

  @Override
  public GenericSequence length(long length) {
    super.length(length);
    return this;
  }

  @Override
  public GenericSequence length(Rational length) {
    super.length(length);
    return this;
  }

  @Override
  public GenericSequence offset(long offset) {
    super.offset(offset);
    return this;
  }

  @Override
  public GenericSequence offset(Rational offset) {
    super.offset(offset);
    return this;
  }

  @Override
  public GenericSequence tick(Tick tick) {
    super.tick(tick);
    return this;
  }

  @Override
  public GenericSequence property(String name, String value) {
    super.property(name, value);
    return this;
  }
}
