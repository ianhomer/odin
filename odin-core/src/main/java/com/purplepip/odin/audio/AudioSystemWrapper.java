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

import com.purplepip.odin.system.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AudioSystemWrapper {
  private final List<MixerWrapper> mixerWrappers;
  private static final AtomicBoolean HAS_DUMPED = new AtomicBoolean(false);

  /**
   * Create a new audio system wrapper.
   */
  public AudioSystemWrapper() {
    mixerWrappers = new ArrayList<>();
    Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
    for (Mixer.Info mixerInfo : mixerInfos) {
      mixerWrappers.add(new MixerWrapper(AudioSystem.getMixer(mixerInfo)));
    }
  }

  private boolean hasMixers() {
    return !mixerWrappers.isEmpty();
  }

  public boolean isAudioOutputSupported() {
    return hasMixers() && Container.getContainer().isAudioEnabled();
  }

  public void dump() {
    dump(false);
  }

  /**
   * Dump system audio information.
   */
  public void dump(boolean dumpOnlyIfNotDumpedBefore) {
    /*
     * Only dump information once for the runtime.
     */
    if (dumpOnlyIfNotDumpedBefore) {
      if (HAS_DUMPED.get()) {
        return;
      }
      HAS_DUMPED.set(true);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\nSYSTEM AUDIO\n");
    sb.append("------------\n");
    if (mixerWrappers.isEmpty()) {
      sb.append("No audio mixers available\n");
    } else {
      sb.append("Mixers\n");
      int i = 0;
      for (MixerWrapper mixer : mixerWrappers) {
        sb.append('\n').append(i++).append(") ").append(mixer.toString());
      }
    }
    LOG.info(sb.toString());
  }

  private class MixerWrapper {
    private final Mixer mixer;

    private MixerWrapper(Mixer mixer) {
      this.mixer = mixer;
    }

    @Override
    public String toString() {
      return new AudioDevice(mixer).getSummary();
    }
  }
}
