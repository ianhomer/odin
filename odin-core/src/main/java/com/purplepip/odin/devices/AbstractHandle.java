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

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public abstract class AbstractHandle<D extends Device> implements Handle<D> {
  private boolean sink;
  private boolean source;
  private boolean enabled;
  private Class<? extends Device> deviceClass;

  protected void initialise() {
    boolean error = false;
    LOG.trace("Initialising {}", this);
    try (D device = open()) {
      sink = device.isSink();
      source = device.isSource();
      deviceClass = device.getClass();
    } catch (DeviceUnavailableException e) {
      LOG.warn("Cannot open device to initialise handle", e);
      error = true;
      sink = false;
      source = false;
    }
    enabled = !error;
    LOG.trace("Initialised {}", this);
  }

  @Override
  public boolean isSink() {
    return sink;
  }

  @Override
  public boolean isSource() {
    return source;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public Class<? extends Device> getDeviceClass() {
    return deviceClass;
  }

  @Override
  public String toString() {
    return getVendor() + " " + getName();
  }
}
