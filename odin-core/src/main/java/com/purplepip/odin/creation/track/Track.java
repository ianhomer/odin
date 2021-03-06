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
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.aspect.Aspect;
import com.purplepip.odin.events.Event;
import java.util.Map;

public interface Track extends Aspect {
  /**
   * Channel for the track.
   *
   * @return channel
   */
  int getChannel();

  /**
   * Look at the next event from this track.
   *
   * @return next event with microsecond time units
   */
  Event peek();

  /**
   * Take next event of this track, with subsequent call to peek or pop getting the subsequent one.
   * one
   *
   * @return next event with microsecond time units
   */
  Event pop();

  /**
   * Runtime tick for the track.
   *
   * @return runtime tick.
   */
  /*
   * TODO : Track should only use microseconds as the unit, why do we need to expose the tick here?
   */
  Tick getTick();

  /**
   * Whether the track is empty.
   *
   * @return is empty.
   */
  boolean isVoid();

  /**
   * Triggers that this track listens for.
   */
  Map<String, Action> getTriggers();

  /**
   * Whether track is enabled.
   */
  boolean isEnabled();

  /**
   * Enable or disable track.
   *
   * @param enabled whether to enable or disable track.
   */
  void setEnabled(boolean enabled);

  void setProperty(String name, String value);

  String getProperty(String name);

  /**
   * Start the track.
   */
  void start();

  /**
   * Stop the track.
   */
  void stop();

  /**
   * Initialise the track.
   */
  @Override
  void initialise();
}
