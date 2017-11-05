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

package com.purplepip.odin.system;

import lombok.extern.slf4j.Slf4j;

/**
 * Singleton describing the container that odin is running in.
 */
@Slf4j
public class Container {
  private static Container container = new Container();

  /*
   * Audio support can be explicitly disabled by setting this system flag to false.
   */
  private static final String AUDIO_ENABLED = "odin.audio.enabled";

  private boolean audioEnabled;

  Container() {
    audioEnabled = !"false".equals(System.getProperty(AUDIO_ENABLED));
    LOG.info("Audio Enabled : {}", audioEnabled);
  }

  public static Container getContainer() {
    return container;
  }


  public boolean isAudioEnabled() {
    return audioEnabled;
  }
}
