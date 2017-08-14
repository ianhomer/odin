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
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.layer.MutableLayer;
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
  private List<ProjectApplyListener> applyListeners = new ArrayList<>();
  private List<ProjectLoadListener> loadListeners = new ArrayList<>();
  private List<ProjectSaveListener> saveListeners = new ArrayList<>();

  private Project project;

  public ProjectContainer() {
    // Empty constructor explicitly supported.
  }

  public ProjectContainer(Project project) {
    this.project = project;
  }

  protected Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getName() {
    return project.getName();
  }

  /**
   * Save project to persistent store.
   */
  public final void save() {
    for (ProjectSaveListener listener : saveListeners) {
      listener.onProjectSave(project);
    }
  }

  /**
   * Load project from persistent store.
   */
  public final void load() {
    for (ProjectLoadListener listener : loadListeners) {
      listener.onProjectLoad(this);
    }
  }

  /**
   * Apply project changes.
   */
  public final void apply() {
    for (ProjectApplyListener listener : applyListeners) {
      listener.onProjectApply(project);
    }
  }



  /**
   * Add project apply listener.
   *
   * @param listener project apply listener.
   */
  public void addApplyListener(ProjectApplyListener listener) {
    applyListeners.add(listener);
  }

  /**
   * Add project load listener.
   *
   * @param listener project load listener.
   */
  public void addLoadListener(ProjectLoadListener listener) {
    loadListeners.add(listener);
  }


  /**
   * Add project save listener.
   *
   * @param listener project save listener.
   */
  public void addSaveListener(ProjectSaveListener listener) {
    saveListeners.add(listener);
  }

  /**
   * Remove project apply listener.
   *
   * @param listener project apply listener
   */
  public void removeApplyListener(ProjectApplyListener listener) {
    applyListeners.remove(listener);
  }

  /**
   * Remove project remove listener.
   *
   * @param listener project remove listener
   */
  public void removeLoadListener(ProjectLoadListener listener) {
    loadListeners.remove(listener);
  }

  /**
   * Remove project save listener.
   *
   * @param listener project save listener
   */
  public void removeSaveListener(ProjectSaveListener listener) {
    saveListeners.remove(listener);
  }

  public Sequence getSequence(long id) {
    return getProject().getSequences().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
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
   * Get sequence stream.
   *
   * @return sequence stream
   */
  public Stream<Sequence> getSequenceStream() {
    return getProject().getSequences().stream();
  }

  /**
   * Add a sequence to the project.
   *
   * @param sequence sequence
   */
  public void addSequence(Sequence sequence) {
    LOG.debug("Adding sequence {}", sequence);
    project.addSequence(sequence);
  }

  public void removeSequence(Sequence sequence) {
    LOG.debug("Removing sequence {}", sequence);
    project.removeSequence(sequence);
  }

  /**
   * Add channel configuration.
   *
   * @param channel configuration to add
   */
  public void addChannel(Channel channel) {
    project.addChannel(channel);
  }

  /**
   * Get channel by ID.
   *
   * @param id channel ID
   * @return channel for the given ID.
   */
  public Channel getChannel(long id) {
    return getProject().getChannels().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
  }

  /**
   * Add layer.
   *
   * @param layer to add
   */
  public void addLayer(MutableLayer layer) {
    project.addLayer(layer);
  }

  /**
   * Layer stream.
   *
   * @return layer stream
   */
  public Stream<Layer> getLayerStream() {
    return project.getLayers().stream();
  }

  /**
   * Remove channel configuration.
   *
   * @param channel configuration to remove
   */
  public void removeChannel(Channel channel) {
    project.removeChannel(channel);
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

  public boolean isEmpty() {
    return project.getChannels().isEmpty() && project.getSequences().isEmpty();
  }
}
