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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Environment {
  private final Set<Handle> identifiers = new HashSet<>();
  private final Set<HandleProvider> providers;

  /**
   * Create an environment.
   *
   * @param providers handle providers
   */
  public Environment(HandleProvider... providers) {
    this.providers = new HashSet<>(Arrays.asList(providers));
    refresh();
  }

  /**
   * Refresh the environment.
   */
  public void refresh() {
    identifiers.clear();
    for (HandleProvider provider : providers) {
      identifiers.addAll(provider.getIdentifiers());
    }
  }

  public boolean isEmpty() {
    return identifiers.isEmpty();
  }

  public Set<Handle> getIdentifiers() {
    return Collections.unmodifiableSet(identifiers);
  }

  /**
   * Dump environment information.
   */
  public void dump() {
    StringBuilder sb = new StringBuilder();
    sb.append("\nENVIRONMENT\n");
    sb.append("------------\n");
    sb.append(asString(true));
    sb.append('\n');
    LOG.info(sb.toString());
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
      for (Handle identifier : getIdentifiers()) {
        sb.append('\n').append(i++).append(") - ");
        i++;
        identifier.appendTo(sb);
        if (withConnections) {
          try {
            identifier.connect(identifier).appendTo(sb);
          } catch (DeviceUnavailableException e) {
            LOG.error("Cannot get device " + identifier, e);
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
