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

package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Project container providing project handling logic independent of project model.
 */
@Slf4j
public class ProjectContainer {
  private List<ProjectListener> listeners = new ArrayList<>();
  private Project project;

  public ProjectContainer() {
    // Empty constructor explicitly supported.
  }

  public ProjectContainer(Project project) {
    this.project = project;
  }

  Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  /**
   * Apply project configuration.
   */
  public void apply() {
    for (ProjectListener listener : listeners) {
      listener.onProjectApply();
    }
  }

  /**
   * Add project listener.
   *
   * @param projectListener project listener.
   */
  public void addListener(ProjectListener projectListener) {
    listeners.add(projectListener);
  }

  /**
   * Remove project listener.
   *
   * @param projectListener project listener
   */
  public void removeListener(ProjectListener projectListener) {
    listeners.remove(projectListener);
  }

  /**
   * Get iterable of the sequences stored in this project.
   *
   * @return iterable of sequences
   */
  public Iterable<Sequence> getSequences() {
    return Collections.unmodifiableSet(getProject().getSequences());
  }

  /**
   * Add a sequence to the project.
   *
   * @param sequence sequence
   */
  public void addSequence(Sequence sequence) {
    LOG.debug("Adding sequence {} with time units {}",
        sequence.getClass().getSimpleName(),
        sequence.getTick().getClass().getSimpleName());
    project.getSequences().add(sequence);
  }

  /**
   * Add channel configuration.
   *
   * @param channel configuration to add
   */
  public void addChannel(Channel channel) {
    project.getChannels().add(channel);
  }


  /**
   * Remove channel configuration.
   *
   * @param channel configuration to remove
   */
  public void removeChannel(Channel channel) {
    project.getChannels().remove(channel);
  }

  public Iterable<Channel> getChannels() {
    return Collections.unmodifiableSet(project.getChannels());
  }

  /**
   * Channel stream.
   *
   * @return channel stream
   */
  public Stream<Channel> getChannelStream() {
    return project.getChannels().stream();
  }
}
