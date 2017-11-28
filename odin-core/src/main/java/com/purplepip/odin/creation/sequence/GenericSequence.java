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

import com.purplepip.odin.clock.tick.AbstractTimeThing;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.action.ActionType;
import com.purplepip.odin.specificity.NameValue;
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
  private String type = new NameValue(this).get();
  private List<String> layers = new ArrayList<>();
  private Map<String, ActionType> triggers = new HashMap<>();

  /**
   * Create new generic sequence.
   */
  public GenericSequence() {
    super();
  }

  public GenericSequence(long id) {
    super(id);
  }

  @Override
  public List<String> getLayers() {
    return layers;
  }

  @Override
  public void addLayer(String layer) {
    LOG.debug("Adding layer : {}", layer);
    layers.add(layer);
  }

  public GenericSequence layer(String layer) {
    addLayer(layer);
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
  public Map<String, ActionType> getTriggers() {
    return triggers;
  }

  @Override
  public void addTrigger(String trigger, ActionType action) {
    LOG.debug("Adding trigger : {} when {} ", action, trigger);
    triggers.put(trigger, action);
  }

  public GenericSequence trigger(String trigger, ActionType action) {
    addTrigger(trigger, action);
    return this;
  }

  @Override
  public void removeTrigger(String trigger) {
    triggers.remove(trigger);
  }

  protected GenericSequence copy(GenericSequence copy) {
    copy.setChannel(this.getChannel());
    copy.setType(this.getType());
    this.getLayers().forEach(copy::addLayer);
    this.getTriggers().forEach(copy::addTrigger);
    super.copy(copy);
    return copy;
  }

  public GenericSequence channel(int channel) {
    this.channel = channel;
    return this;
  }

  public GenericSequence type(String type) {
    this.type = type;
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
  public GenericSequence tick(Tick tick) {
    super.tick(tick);
    return this;
  }

}
