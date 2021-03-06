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

package com.purplepip.odin.performance;

import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Performance container providing performance handling logic independent of performance model.
 */
@Slf4j
public class DefaultPerformanceContainer implements PerformanceContainer {
  private final List<PerformanceApplyListener> applyListeners = new ArrayList<>();
  private final List<PerformanceLoadListener> loadListeners = new ArrayList<>();
  private final List<PerformanceSaveListener> saveListeners = new ArrayList<>();

  private Performance performance;

  public DefaultPerformanceContainer() {
    // Empty constructor explicitly supported.
  }

  public DefaultPerformanceContainer(Performance performance) {
    this.performance = performance;
  }

  @Override
  public Performance getPerformance() {
    return performance;
  }

  @Override
  public void setPerformance(Performance performance) {
    this.performance = performance;
  }

  @Override
  public String getName() {
    return performance.getName();
  }

  /**
   * Save performance to persistent store.
   */
  @Override
  public final void save() {
    for (PerformanceSaveListener listener : saveListeners) {
      listener.onPerformanceSave(performance);
    }
  }

  /**
   * Load performance from persistent store.
   */
  @Override
  public final void load() {
    for (PerformanceLoadListener listener : loadListeners) {
      listener.onPerformanceLoad(this);
    }
  }

  /**
   * Apply performance changes.
   */
  @Override
  public final void apply() {
    for (PerformanceApplyListener listener : applyListeners) {
      listener.onPerformanceApply(performance);
    }
  }



  /**
   * Add performance apply listener.
   *
   * @param listener performance apply listener.
   */
  @Override
  public void addApplyListener(PerformanceApplyListener listener) {
    applyListeners.add(listener);
  }

  /**
   * Add performance load listener.
   *
   * @param listener performance load listener.
   */
  @Override
  public void addLoadListener(PerformanceLoadListener listener) {
    loadListeners.add(listener);
  }


  /**
   * Add performance save listener.
   *
   * @param listener performance save listener.
   */
  @Override
  public void addSaveListener(PerformanceSaveListener listener) {
    saveListeners.add(listener);
  }

  /**
   * Remove performance apply listener.
   *
   * @param listener performance apply listener
   */
  public void removeApplyListener(PerformanceApplyListener listener) {
    applyListeners.remove(listener);
  }

  /**
   * Remove performance remove listener.
   *
   * @param listener performance remove listener
   */
  public void removeLoadListener(PerformanceLoadListener listener) {
    loadListeners.remove(listener);
  }

  /**
   * Remove performance save listener.
   *
   * @param listener performance save listener
   */
  public void removeSaveListener(PerformanceSaveListener listener) {
    saveListeners.remove(listener);
  }

  @Override
  public SequenceConfiguration getSequence(long id) {
    return getPerformance().getSequences().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
  }

  /**
   * Get iterable of the sequences stored in this performance.
   *
   * @return iterable of sequences
   */
  @Override
  public Iterable<SequenceConfiguration> getSequences() {
    return Collections.unmodifiableSet(getPerformance().getSequences());
  }

  /**
   * Get sequence stream.
   *
   * @return sequence stream
   */
  @Override
  public Stream<SequenceConfiguration> getSequenceStream() {
    return getPerformance().getSequences().stream();
  }

  /**
   * Add a sequence to the performance.
   *
   * @param sequence sequence
   */
  @Override
  public DefaultPerformanceContainer addSequence(SequenceConfiguration sequence) {
    LOG.debug("Adding sequence {}", sequence);
    performance.addSequence(sequence);
    return this;
  }

  @Override
  public void removeSequence(SequenceConfiguration sequence) {
    LOG.debug("Removing sequence {}", sequence);
    performance.removeSequence(sequence);
  }

  /**
   * Add channel configuration.
   *
   * @param channel configuration to add
   */
  @Override
  public DefaultPerformanceContainer addChannel(Channel channel) {
    performance.addChannel(channel);
    return this;
  }

  /**
   * Get channel by ID.
   *
   * @param id channel ID
   * @return channel for the given ID.
   */
  @Override
  public Channel getChannel(long id) {
    return getPerformance().getChannels().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
  }

  /**
   * Add layer.
   *
   * @param layer to add
   */
  @Override
  public DefaultPerformanceContainer addLayer(Layer layer) {
    performance.addLayer(layer);
    return this;
  }

  /**
   * Get layer by ID.
   *
   * @param id layer ID
   * @return layer for the given ID.
   */
  @Override
  public Layer getLayer(long id) {
    return getPerformance().getLayers().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
  }

  /**
   * Layer stream.
   *
   * @return layer stream
   */
  @Override
  public Stream<Layer> getLayerStream() {
    return performance.getLayers().stream();
  }

  /**
   * Add trigger.
   *
   * @param trigger to add
   */
  @Override
  public DefaultPerformanceContainer addTrigger(TriggerConfiguration trigger) {
    performance.addTrigger(trigger);
    return this;
  }

  /**
   * Get trigger by ID.
   *
   * @param id layer ID
   * @return layer for the given ID.
   */
  @Override
  public TriggerConfiguration getTrigger(long id) {
    return getPerformance().getTriggers().stream().filter(s -> s.getId() == id)
        .findFirst().orElse(null);
  }

  /**
   * Trigger stream.
   *
   * @return trigger stream
   */
  @Override
  public Stream<TriggerConfiguration> getTriggerStream() {
    return performance.getTriggers().stream();
  }

  /**
   * Remove channel configuration.
   *
   * @param channel configuration to remove
   */
  @Override
  public void removeChannel(Channel channel) {
    performance.removeChannel(channel);
  }

  @Override
  public Iterable<Channel> getChannels() {
    return Collections.unmodifiableSet(performance.getChannels());
  }

  /**
   * Channel stream.
   *
   * @return channel stream
   */
  @Override
  public Stream<Channel> getChannelStream() {
    return performance.getChannels().stream();
  }

  @Override
  public boolean isEmpty() {
    return performance == null || (!hasChannels() && !hasSequences() && !hasLayers());
  }

  /**
   * Whether this performance have any channels.
   *
   * @return whether performance has any channels
   */
  public boolean hasChannels() {
    /*
     * Null safety since channels collection might not have initialised yet in the persistent
     * domain.
     */
    return performance.getChannels() != null && !performance.getChannels().isEmpty();
  }

  /**
   * Whether this performance have any sequences.
   *
   * @return whether performance has any sequences
   */
  public boolean hasSequences() {
    return performance.getSequences() != null && !performance.getSequences().isEmpty();
  }

  /**
   * Whether this performance have any layers.
   *
   * @return whether performance has any layers
   */
  public boolean hasLayers() {
    return performance.getLayers() != null && !performance.getLayers().isEmpty();
  }

  /**
   * Mixin performance.
   *
   * @param mixin to mix in
   */
  @Override
  public void mixin(Performance mixin) {
    this.performance.mixin(mixin);
  }
}
