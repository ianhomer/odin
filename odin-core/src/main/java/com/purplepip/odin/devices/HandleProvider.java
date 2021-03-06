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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/** A provider that gives handles on to devices. */
public interface HandleProvider {
  /**
   * Handle classes that this handle provider can return.
   *
   * @return handle classes
   */
  Set<Class<? extends Handle>> getHandleClasses();

  /**
   * Get sink handles for this provider, i.e. handles for devices that can consume events
   *
   * @return sink handles
   */
  Set<Handle> getSinkHandles();

  /**
   * Get source handles for this provider, i.e. handles for devices that can provide events.
   *
   * @return source handles
   */
  Set<Handle> getSourceHandles();

  default Optional<Handle> findOneSink() {
    return getSinkHandles().stream().findFirst();
  }

  /**
   * Find first sink by class.
   *
   * @param deviceClass device class to match device of returned handle
   * @return first sink handle referencing device of the given device class
   */
  default Optional<Handle> findFirstSinkByClass(Class<? extends Device> deviceClass) {
    return getSinkHandles()
        .stream()
        .filter(handle -> deviceClass.equals(handle.getDeviceClass()))
        .findFirst();
  }

  default Handle findFirstSinkByClassOrElseThrow(Class<? extends Device> deviceClass)
      throws DeviceUnavailableException {
    return findFirstSinkByClass(deviceClass).orElseThrow(DeviceUnavailableException::new);
  }

  default Stream<Handle> findAllSinks() {
    return getSinkHandles().stream();
  }

  default Optional<Handle> findOneSource() {
    return getSourceHandles().stream().findFirst();
  }

  default Stream<Handle> findAllSources() {
    return getSourceHandles().stream();
  }
}
