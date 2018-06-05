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

@ToString
public class MockHandle implements Handle<MockDevice> {
  private final String name;
  private final boolean sink;
  private final boolean source;
  private final boolean enabled;
  private final boolean openOk;

  /**
   * Create mock handle.
   *
   * @param name name
   * @param sink is sink
   * @param source is source
   * @param enabled enabled
   */
  public MockHandle(String name, boolean sink, boolean source, boolean enabled,
                    boolean openOk) {
    this.name = name;
    this.sink = sink;
    this.source = source;
    this.enabled = enabled;
    this.openOk = openOk;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVendor() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getType() {
    return null;
  }

  @Override
  public MockDevice open() throws DeviceUnavailableException {
    if (openOk) {
      return new MockDevice(this);
    } else {
      throw new DeviceUnavailableException("Cannot open " + name);
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean isSink() {
    return sink;
  }

  @Override
  public boolean isSource() {
    return source;
  }
}
