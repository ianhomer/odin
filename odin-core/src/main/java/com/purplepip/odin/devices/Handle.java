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

/**
 * A handle onto a device that we open, to get a device.
 */
public interface Handle<D extends Device> {
  String getName();

  String getVendor();

  String getDescription();

  String getType();

  Class<? extends Device> getDeviceClass();

  D open() throws DeviceUnavailableException;

  boolean isEnabled();

  boolean isSink();

  boolean isSource();

  /**
   * Serialised string version of this identifier to a StringBuilder.
   *
   * @param sb StringBuilder to serialise to
   */
  default void appendTo(StringBuilder sb) {
    sb.append(getVendor());
    sb.append(" - ").append(getName());
    sb.append(" - ").append(getDescription());
  }
}
