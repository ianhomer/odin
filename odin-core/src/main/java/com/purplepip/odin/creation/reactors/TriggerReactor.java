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

import com.purplepip.odin.creation.plugin.PluggableAspect;
import com.purplepip.odin.creation.sequence.Action;
import com.purplepip.odin.creation.sequence.ActionOperation;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.creation.triggers.Trigger;
import com.purplepip.odin.creation.triggers.TriggerConfiguration;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.operation.Operation;
import java.util.ArrayList;
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
  private Map<Track, Action> trackActions = new HashMap<>();
  private TriggerFactory triggerFactory;

  public TriggerReactor(TriggerFactory triggerFactory) {
    this.triggerFactory = triggerFactory;
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
        ripples.add(new ActionOperation(action, track.getName(), operation));
        switch (action)  {
          case ENABLE:
            track.setEnabled(true);
            break;
          case DISABLE:
            track.setEnabled(false);
            break;
          case RESET:
            track.reset();
            break;
          case START:
            track.start();
            break;
          case STOP:
            track.stop();
            break;
          default:
            LOG.warn("Trigger action {} not supported", action);
        }
      });
      return ripples;
    }
    return null;
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
    trigger.inject(sequence);
  }
}
