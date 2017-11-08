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

package com.purplepip.odin.sequence.reactors;

import com.purplepip.odin.sequence.track.Track;
import com.purplepip.odin.sequence.triggers.Action;
import com.purplepip.odin.sequence.triggers.Trigger;
import com.purplepip.odin.sequence.triggers.TriggerConfiguration;
import com.purplepip.odin.sequence.triggers.TriggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

public class TriggerReactor implements Reactor {
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
  public TriggerConfiguration getConfiguration() {
    return triggerConfiguration;
  }

  @Override
  public Trigger getPlugin() {
    return trigger;
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
}
