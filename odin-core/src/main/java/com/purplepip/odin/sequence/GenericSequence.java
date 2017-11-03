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

package com.purplepip.odin.sequence;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.tick.AbstractTimeThing;
import com.purplepip.odin.sequence.triggers.Action;
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
@ToString(exclude = "project", callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class GenericSequence extends AbstractTimeThing implements MutableSequenceConfiguration {
  private int channel;
  private String type;
  private Project project;
  private List<String> layers = new ArrayList<>();
  private Map<String, Action> triggers = new HashMap<>();

  public GenericSequence() {
    super();
  }

  public GenericSequence(String name) {
    super(name);
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

  @Override
  public void removeLayer(String layer) {
    layers.remove(layer);
  }

  @Override
  public boolean isEmpty() {
    return layers.isEmpty();
  }

  @Override
  public Map<String, Action> getTriggers() {
    return triggers;
  }

  @Override
  public void addTrigger(String trigger, Action action) {
    LOG.debug("Adding trigger : {} when {} ", action, trigger);
    triggers.put(trigger, action);
  }

  @Override
  public void removeTrigger(String trigger) {
    triggers.remove(trigger);
  }

  protected GenericSequence copy(GenericSequence copy, GenericSequence original) {
    copy.setChannel(original.getChannel());
    copy.setType(original.getType());
    copy.setProject(original.getProject());
    original.getLayers().forEach(copy::addLayer);
    original.getTriggers().forEach(copy::addTrigger);
    super.copy(copy, original);
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
}
