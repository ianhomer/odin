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

package com.purplepip.odin.devices;

import com.purplepip.odin.common.OdinException;
import javax.sound.midi.MidiUnavailableException;

public class DeviceUnavailableException extends OdinException {
  public DeviceUnavailableException() {
    super();
  }

  public DeviceUnavailableException(String description) {
    super(description);
  }

  public DeviceUnavailableException(MidiUnavailableException e) {
    super(e);
  }

  public DeviceUnavailableException(String description, Exception e) {
    super(description, e);
  }
}
