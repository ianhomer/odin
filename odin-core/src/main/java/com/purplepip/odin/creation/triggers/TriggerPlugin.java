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

package com.purplepip.odin.creation.triggers;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.creation.plugin.Plugin;
import com.purplepip.odin.math.Rational;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

public abstract class TriggerPlugin implements Trigger, Plugin, MutableTriggerConfiguration {
  private final GenericTrigger trigger;

  protected TriggerPlugin() {
    trigger = new GenericTrigger(getClass());
  }

  @Override
  public boolean isEnabled() {
    return trigger.isEnabled();
  }

  @Override
  public @NotNull Tick getTick() {
    return trigger.getTick();
  }

  @Override
  public @NotNull Rational getLength() {
    return trigger.getLength();
  }

  @Override
  public @NotNull Rational getOffset() {
    return trigger.getOffset();
  }

  @Override
  public long getId() {
    return trigger.getId();
  }

  @Override
  public @NotNull String getName() {
    return trigger.getName();
  }

  @Override
  public String getProperty(String name) {
    return trigger.getProperty(name);
  }

  @Override
  public Stream<String> getPropertyNames() {
    return trigger.getPropertyNames();
  }

  @Override
  public Stream<Map.Entry<String, String>> getPropertyEntries() {
    return trigger.getPropertyEntries();
  }

  @Override
  public boolean hasProperties() {
    return trigger.hasProperties();
  }

  protected void copy(TriggerPlugin copy) {
    trigger.copy(copy.trigger);
  }

  public TriggerPlugin name(String s) {
    trigger.setName(s);
    return this;
  }

  @Override
  public void setEnabled(boolean enabled) {
    trigger.setEnabled(enabled);
  }

  @Override
  public void setTick(Tick tick) {
    trigger.setTick(tick);
  }

  @Override
  public void setLength(Rational length) {
    trigger.setLength(length);
  }

  @Override
  public void setOffset(Rational offset) {
    trigger.setOffset(offset);
  }

  @Override
  public void setId(long id) {
    trigger.setId(id);
  }

  @Override
  public void setName(String name) {
    trigger.setName(name);
  }

  @Override
  public void setProperty(String name, String value) {
    trigger.setProperty(name, value);
  }

  protected void registerDependency(String dependency) {
    trigger.registerDependency(dependency);
  }

  public String getType() {
    return trigger.getType();
  }

  public Stream<String> dependsOn() {
    return trigger.dependsOn();
  }
}
