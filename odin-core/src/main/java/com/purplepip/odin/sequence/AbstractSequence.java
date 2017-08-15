/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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
import com.purplepip.odin.sequence.tick.Tick;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract sequence.
 */
@ToString(exclude = "project")
@Slf4j
public abstract class AbstractSequence implements MutableSequence {
  /*
   * Cheap ID generator for default patterns.  Note that persistence implementation used for
   * the runtime has a more robust ID generation mechanism, however for the transient usage,
   * this cheap generator is good enough.
   */
  private static final AtomicLong LAST_PATTERN_ID = new AtomicLong();
  protected long id = LAST_PATTERN_ID.incrementAndGet();
  private String name;

  private Tick tick;
  private long length = -1;
  private long offset;
  private int channel;
  private String flowName;
  private Project project;
  private Set<String> layers = new HashSet<>();

  /**
   * ID auto generated.
   */
  public AbstractSequence() {
    id = LAST_PATTERN_ID.incrementAndGet();
  }

  /**
   * ID taken from constructor.
   */
  public AbstractSequence(long id) {
    setId(id);
  }

  @Override
  public long getId() {
    return id;
  }

  protected void setId(long id) {
    this.id = id;
    /*
     * Auto generate name if one has not been provided.
     */
    if (name == null) {
      setName(String.valueOf(id));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setTick(Tick tick) {
    this.tick = tick;
  }

  @Override
  public Tick getTick() {
    return tick;
  }

  /**
   * Set the length of the series in ticks.
   *
   * @param length length of series in ticks
   */
  @Override
  public void setLength(long length) {
    this.length = length;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public void setOffset(long offset) {
    this.offset = offset;
  }

  @Override
  public long getOffset() {
    return offset;
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
  public Set<String> getLayers() {
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
}
