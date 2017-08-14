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

package com.purplepip.odin.sequence.layer;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.ToString;

/**
 * Default layer implementation.
 */
@ToString(exclude = "project")
public class DefaultLayer implements MutableLayer {
  /*
   * Cheap ID generator for default layers.  Note that persistence implementation used for
   * the runtime has a more robust ID generation mechanism, however for the transient usage,
   * this cheap generator is good enough.
   */
  private static final AtomicLong LAST_PATTERN_ID = new AtomicLong();
  protected long id = LAST_PATTERN_ID.incrementAndGet();

  private Project project;
  private String name;
  private Tick tick;
  private long length;
  private long offset;

  private Set<Layer> parents = new HashSet<>();

  public DefaultLayer() {
  }

  public DefaultLayer(String name) {
    this.name = name;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<Layer> getParents() {
    return parents;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setTick(Tick tick) {
    this.tick = tick;
  }

  @Override
  public Tick getTick() {
    return tick;
  }

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
}
