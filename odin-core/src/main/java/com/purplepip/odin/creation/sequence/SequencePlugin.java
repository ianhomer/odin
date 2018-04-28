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
import com.purplepip.odin.clock.tick.MutableTimeThing;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.action.ActionConfiguration;
import com.purplepip.odin.creation.plugin.Plugin;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Specific sequence plugins must extend this SequencePlugin class.
 */
@ToString
@EqualsAndHashCode
public abstract class SequencePlugin
    implements MutableSequenceConfiguration, MutableTimeThing, Plugin, Sequence,
    ThingConfiguration {
  /*
   * Implement plugin as a wrapper of a generic sequence to keep tight control on what is
   * exposed as the public plugin interface.
   */
  private final GenericSequence sequence;

  protected SequencePlugin() {
    sequence = new GenericSequence(getClass());
  }

  public SequencePlugin layer(ThingName... layerNames) {
    sequence.layer(layerNames);
    return this;
  }

  public SequencePlugin layer(String... layerNames) {
    sequence.layer(layerNames);
    return this;
  }

  public void setChannel(int channel) {
    sequence.setChannel(channel);
  }

  @Override
  public void addLayer(String layerName) {
    sequence.addLayer(layerName);
  }

  @Override
  public void removeLayer(String layerName) {
    sequence.removeLayer(layerName);
  }

  @Override
  public void addTrigger(String triggerName, ActionConfiguration action) {
    sequence.addTrigger(triggerName, action);
  }

  @Override
  public void removeTrigger(String triggerName) {
    sequence.removeTrigger(triggerName);
  }

  public SequencePlugin channel(int channel) {
    sequence.setChannel(channel);
    return this;
  }

  public SequencePlugin enabled(boolean enabled) {
    sequence.setEnabled(enabled);
    return this;
  }

  @Override
  public void setEnabled(boolean enabled) {
    sequence.setEnabled(enabled);
  }

  @Override
  public void setTick(Tick tick) {
    sequence.setTick(tick);
  }

  public void setLength(Rational length) {
    sequence.setLength(length);
  }

  @Override
  public void setOffset(Rational offset) {
    sequence.setOffset(offset);
  }

  public SequencePlugin length(long length) {
    sequence.setLength(Wholes.valueOf(length));
    return this;
  }

  public SequencePlugin name(String name) {
    sequence.setName(name);
    return this;
  }

  public SequencePlugin offset(long offset) {
    sequence.setOffset(Wholes.valueOf(offset));
    return this;
  }

  public SequencePlugin trigger(String trigger, ActionConfiguration... actions) {
    sequence.trigger(trigger, actions);
    return this;
  }

  @Override
  public void setId(long id) {
    sequence.setId(id);
  }

  @Override
  public void setName(String name) {
    sequence.setName(name);
  }

  @Override
  public int getChannel() {
    return sequence.getChannel();
  }

  @Override
  public @NotNull List<String> getLayers() {
    return sequence.getLayers();
  }

  /**
   * Set layers.
   *
   * @param layers layers to set
   */
  public void setLayers(List<String> layers) {
    for (String layerName : layers) {
      sequence.addLayer(layerName);
    }
  }

  @Override
  public boolean isEmpty() {
    return sequence.isEmpty();
  }

  @Override
  public Map<String, ActionConfiguration> getTriggers() {
    return sequence.getTriggers();
  }

  @Override
  public boolean isEnabled() {
    return sequence.isEnabled();
  }

  @Override
  public @NotNull Tick getTick() {
    return sequence.getTick();
  }

  @Override
  public @NotNull Rational getLength() {
    return sequence.getLength();
  }

  @Override
  public @NotNull Rational getOffset() {
    return sequence.getOffset();
  }

  @Override
  public long getId() {
    return sequence.getId();
  }

  @Override
  public @NotNull String getName() {
    return sequence.getName();
  }

  @Override
  public String getProperty(String name) {
    return sequence.getProperty(name);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return sequence.getPropertyNames();
  }

  @Override
  public Stream<Map.Entry<String, String>> getPropertyEntries() {
    return sequence.getPropertyEntries();
  }

  @Override
  public boolean hasProperties() {
    return sequence.hasProperties();
  }

  protected SequencePlugin copy(SequencePlugin copy) {
    sequence.copy(copy.sequence);
    return copy;
  }

  @Override
  public void setProperty(String name, String value) {
    sequence.setProperty(name, value);
  }

  public SequencePlugin tick(Tick tick) {
    sequence.setTick(tick);
    return this;
  }

  public String getType() {
    return sequence.getType();
  }
}
