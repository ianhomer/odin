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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.ToString;

/**
 * A named handle is one that has been provided by name, but does not necessarily match to
 * a specific handle in the environment.  A named handle cannot be connected to, but instead
 * just used for comparisons.
 */
@ToString
public class NamedHandle implements Handle {
  private final String description;
  private final String name;
  private final String type;
  private final String vendor;

  public static List<Handle> asHandleList(String... names) {
    return Stream.of(names).map(NamedHandle::new).collect(Collectors.toList());
  }

  public NamedHandle(String name) {
    this(name,null, null, null);
  }

  /**
   * Create new named handle.
   *
   * @param name name
   * @param vendor vendor
   * @param type type
   * @param description description
   */
  public NamedHandle(String name, String vendor, String type, String description) {
    this.name = name;
    this.vendor = vendor;
    this.type = type;
    this.description = description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVendor() {
    return vendor;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public Device open() throws DeviceUnavailableException {
    throw new DeviceUnavailableException("Cannot open a named handle : " + this);
  }
}
