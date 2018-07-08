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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public abstract class AbstractDevice implements Device {
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Map<String, String> properties = new LinkedHashMap<>();
  private final Map<String, String> unmodifiableProperties =
      Collections.unmodifiableMap(properties);

  protected void setProperty(String name, long value) {
    setProperty(name, String.valueOf(value));
  }

  protected void setProperty(String name, int value) {
    setProperty(name, String.valueOf(value));
  }

  protected void setProperty(String name, String value) {
    properties.put(name, value);
  }

  protected void setProperty(String name, int i, String childName, String value) {
    properties.put(name + "[" + i + "]." + childName, value);
  }

  public String getProperty(String name) {
    return properties.get(name);
  }

  protected void initialise() {
  }

  public Map<String, String> getProperties() {
    return unmodifiableProperties;
  }

  @Override
  public void close() {
    // Closing a device takes a few seconds, so we make this asynchronous
    LOG.trace("Requesting closure of device {}", getHandle());
    executor.submit(() -> {
      long start = System.currentTimeMillis();
      LOG.trace("Closing device {}", getHandle());
      deviceClose();
      long delta = System.currentTimeMillis() - start;
      LOG.trace("Closed device {} in {}ms", getHandle(), delta);
      if (delta > 2) {
        LOG.debug("Slow closure of device {} in {}ms", getHandle(), delta);
      }
    });
    LOG.trace("Requested closure of device {}", getHandle());
  }

  protected void deviceClose() {
    // No operation by default
  }
}
