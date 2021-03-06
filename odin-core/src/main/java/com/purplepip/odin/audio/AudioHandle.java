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

package com.purplepip.odin.audio;

import com.purplepip.odin.devices.AbstractHandle;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AudioHandle extends AbstractHandle<AudioDevice> {
  private final Mixer.Info mixerInfo;

  AudioHandle(Mixer.Info mixerInfo) {
    this.mixerInfo = mixerInfo;
    initialise();
  }

  @Override
  public String getName() {
    return mixerInfo.getName();
  }

  @Override
  public String getVendor() {
    return mixerInfo.getVendor();
  }

  @Override
  public String getDescription() {
    return mixerInfo.getDescription();
  }

  @Override
  public String getType() {
    return "audio";
  }

  @Override
  public AudioDevice open() {
    return new AudioDevice(this, AudioSystem.getMixer(mixerInfo));
  }
}
