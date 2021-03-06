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

import static com.purplepip.odin.system.Environments.newAudioEnvironment;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AudioSystemWrapper {
  private static final AtomicBoolean HAS_DUMPED = new AtomicBoolean(false);

  public void dump() {
    dump(false);
  }

  /**
   * Dump system audio information.
   *
   * @param dumpOnlyIfNotDumpedBefore dump only if not dumped before
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

    newAudioEnvironment().dump("SYSTEM AUDIO");
  }
}
