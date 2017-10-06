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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract sequence.
 */
@ToString(exclude = "project")
@Slf4j
public class DefaultSequence extends AbstractTimeThing implements MutableSequence {
  private int channel;
  private String flowName;
  private Project project;
  private List<String> layers = new ArrayList<>();
  private Map<String, String> values = new HashMap<>();
  private List<String> triggers = new ArrayList<>();

  public DefaultSequence() {
    super();
  }

  public DefaultSequence(String name) {
    super(name);
  }

  public DefaultSequence(long id) {
    super(id);
  }

  @Override
  public String getProperty(String propertyName) {
    return values.get(propertyName);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return values.keySet().stream();
  }

  @Override
  public void setProperty(String propertyName, String value) {
    values.put(propertyName, value);
  }

  @Override
  public void setChannel(int channel) {
    this.channel = channel;
  }

  @Override
  public int getChannel() {
    return channel;
  }

  @Override
  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  @Override
  public String getFlowName() {
    return flowName;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
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
  public List<String> getTriggers() {
    return triggers;
  }

  @Override
  public void addTrigger(String trigger) {
    LOG.debug("Adding trigger : {}", trigger);
    triggers.add(trigger);
  }

  @Override
  public void removeTrigger(String trigger) {
    triggers.remove(trigger);
  }

}
