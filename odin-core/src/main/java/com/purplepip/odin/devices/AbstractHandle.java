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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractHandle implements Handle {
  private boolean sink;
  private boolean source;
  private boolean enabled;

  protected void initialise() {
    Device device = null;
    boolean error = false;
    try {
      device = open();
    } catch (DeviceUnavailableException e) {
      LOG.warn("Cannot open device to initialise handle", e);
      error = true;
    }
    enabled = !error;
    sink = device != null && device.isSink();
    source = device != null && device.isSource();
  }

  @Override
  public boolean isSource() {
    return sink;
  }

  @Override
  public boolean isSink() {
    return source;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
