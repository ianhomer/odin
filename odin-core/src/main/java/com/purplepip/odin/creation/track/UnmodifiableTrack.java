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

package com.purplepip.odin.creation.track;

import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.events.Event;
import java.util.Map;
import lombok.ToString;

@ToString
public class UnmodifiableTrack implements Track {
  private Track underlyingTrack;

  public UnmodifiableTrack(Track track) {
    this.underlyingTrack = track;
  }

  @Override
  public long getId() {
    return underlyingTrack.getId();
  }

  @Override
  public String getName() {
    return underlyingTrack.getName();
  }

  @Override
  public int getChannel() {
    return underlyingTrack.getChannel();
  }

  @Override
  public Event peek() {
    return underlyingTrack.peek();
  }

  @Override
  public Event pop() {
    return underlyingTrack.pop();
  }

  @Override
  public Tick getTick() {
    return underlyingTrack.getTick();
  }

  @Override
  public boolean isVoid() {
    return underlyingTrack.isVoid();
  }

  @Override
  public Map<String, Action> getTriggers() {
    return underlyingTrack.getTriggers();
  }

  @Override
  public boolean isEnabled() {
    return underlyingTrack.isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {
    underlyingTrack.setEnabled(enabled);
  }

  @Override
  public void setProperty(String name, String value) {
    throw new OdinRuntimeException(getName() + " is unmodifiable track, can not set "
        + name + " to " + value);
  }

  @Override
  public String getProperty(String name) {
    return underlyingTrack.getProperty(name);
  }

  @Override
  public void start() {
    underlyingTrack.start();
  }

  @Override
  public void stop() {
    underlyingTrack.stop();
  }

  @Override
  public void initialise() {
    underlyingTrack.initialise();
  }
}
