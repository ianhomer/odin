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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Environment {
  private final Set<Handle> handles = new HashSet<>();
  private final Set<HandleProvider> providers;
  private final Set<Class<? extends Handle>> clazzes = new HashSet<>();

  /**
   * Create an environment.
   *
   * @param providers handle providers
   */
  public Environment(HandleProvider... providers) {
    this.providers = new HashSet<>(Arrays.asList(providers));
    Stream.of(providers).forEach(provider -> clazzes.addAll(provider.getHandleClasses()));
    refresh();
  }

  /**
   * Refresh the environment.
   */
  public void refresh() {
    handles.clear();
    for (HandleProvider provider : providers) {
      handles.addAll(provider.getHandles());
    }
  }

  public boolean isEmpty() {
    return handles.isEmpty();
  }

  public Set<Handle> getHandles() {
    return Collections.unmodifiableSet(handles);
  }

  /**
   * Get handles for a give handle class.
   *
   * @param clazz handle class
   * @return handles for the given handle class
   */
  private Set<Handle> getHandles(Class<? extends Handle> clazz) {
    return Collections.unmodifiableSet(
        handles.stream().filter(handle -> handle.getClass().isAssignableFrom(clazz))
          .collect(Collectors.toSet())
    );
  }

  /**
   * Dump environment information.
   */
  public void dump() {
    LOG.info("\n"
        + "ENVIRONMENT\n"
        + "------------\n"
        + asString(true) + '\n'
    );
  }

  /**
   * Return the environment as a string.
   *
   * @param withConnections if true then return an details on the devices connected by the handles.
   * @return environment as a string
   */
  public String asString(boolean withConnections) {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) {
      sb.append("No devices available");
    } else {
      sb.append("Devices\n");
      int i = 0;
      for (Class<? extends Handle> clazz : clazzes) {
        sb.append("\n ** ").append(clazz.getSimpleName()).append(" **\n");
        for (Handle identifier : getHandles(clazz)) {
          sb.append('\n').append(i++).append(") - ");
          identifier.appendTo(sb);
          if (withConnections) {
            try {
              identifier.connect().appendInfoTo(sb);
            } catch (DeviceUnavailableException e) {
              LOG.error("Cannot get device " + identifier, e);
            }
          }
        }

      }
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return asString(false);
  }
}
