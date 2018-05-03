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

package com.purplepip.odin.creation.reactors;

import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionContext;
import com.purplepip.odin.creation.action.ActionOperation;
import com.purplepip.odin.creation.plugin.PluggableAspect;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.triggers.Trigger;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.operation.Operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriggerReactor implements Reactor, PluggableAspect<TriggerConfiguration> {
  private TriggerConfiguration triggerConfiguration;
  private Trigger trigger;
  private final Map<Track, Action> trackActions = new HashMap<>();
  private final TriggerFactory triggerFactory;

  public TriggerReactor(TriggerConfiguration triggerConfiguration, TriggerFactory triggerFactory) {
    this.triggerFactory = triggerFactory;
    setConfiguration(triggerConfiguration);
  }

  @Override
  public long getId() {
    return trigger.getId();
  }

  @Override
  @NotNull
  public String getName() {
    return trigger.getName();
  }

  @Override
  public void initialise() {
    LOG.warn("Initialise on {} ignored", this);
  }

  @Override
  public void setConfiguration(TriggerConfiguration triggerConfiguration) {
    this.triggerConfiguration = triggerConfiguration;
    trigger = triggerFactory.newInstance(triggerConfiguration);
  }

  @Override
  public boolean isVoid() {
    return false;
  }

  @Override
  public TriggerConfiguration getConfiguration() {
    return triggerConfiguration;
  }

  public void addTrackAction(Track track, Action action) {
    trackActions.put(track, action);
  }

  public void removeTrackAction(Track track) {
    trackActions.remove(track);
  }

  public Stream<Map.Entry<Track, Action>> getTracks() {
    return trackActions.entrySet().stream();
  }

  public void clearTrackActions() {
    trackActions.clear();
  }

  @Override
  public List<Operation> react(Operation operation) {
    if (trigger.isTriggeredBy(operation)) {
      List<Operation> ripples = new ArrayList<>();
      LOG.debug("Trigger {} triggered", trigger.getName());
      getTracks().forEach(entry -> {
        Track track = entry.getKey();
        Action action = entry.getValue();
        LOG.debug("Track {} triggered with {}", track.getName(), action);
        action.getRipples().forEach(rippleAction
            -> ripples.add(new ActionOperation(rippleAction, track.getName(), operation)));
        action.execute(new ActionContext(track));
      });
      return ripples;
    }
    return Collections.emptyList();
  }

  /**
   * Get stream of sequence names that this reactor depends on.
   *
   * @return stream of sequence names that this trigger depends on
   */
  public Stream<String> dependsOn() {
    return trigger.dependsOn();
  }

  public void inject(SequenceConfiguration sequence) {
    LOG.debug("Injecting sequence {} into trigger {}", sequence.getName(), trigger.getName());
    trigger.inject(sequence);
  }
}
