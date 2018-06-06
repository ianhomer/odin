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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Environment {
  private final Set<Handle> handles = new HashSet<>();
  private final Set<HandleProvider> providers = new HashSet<>();
  private final Set<Class<? extends Handle>> clazzes = new HashSet<>();

  /**
   * Create an environment.
   *
   * @param providers handle providers
   */
  public Environment(HandleProvider... providers) {
    this(Arrays.stream(providers));
  }

  /**
   * Create an environment.
   *
   * @param providerStream handle provider stream
   */
  public Environment(Stream<HandleProvider> providerStream) {
    providerStream.forEach(provider -> {
      clazzes.addAll(provider.getHandleClasses());
      providers.add(provider);
    });
    refresh();
  }

  /**
   * Refresh the environment.
   */
  public void refresh() {
    handles.clear();
    for (HandleProvider provider : providers) {
      handles.addAll(provider.getSinkHandles());
      handles.addAll(provider.getSourceHandles());
    }
  }

  public boolean isEmpty() {
    return handles.isEmpty();
  }

  /**
   * Stream of devices.
   *
   * @return stream of devices
   */
  public Stream<Device> devices() {
    return handles.stream().map(handle -> {
      try {
        return handle.open();
      } catch (DeviceUnavailableException e) {
        return new UnavailableDevice(handle);
      }
    });
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
  private <H extends Handle> Set<H> getHandles(Class<H> clazz) {
    return Collections.unmodifiableSet(
        handles.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toSet())
    );
  }

  /**
   * Find one sink of a given handle class.
   *
   * @param clazz handle class
   * @param <D> device class type
   * @return handle of sink
   */
  public <D extends Device> Optional<Handle<D>> findOneSinkHandle(
      Class<? extends Handle<D>> clazz) {
    return findOneProvider(clazz)
        .flatMap(HandleProvider::findOneSink)
        .map(clazz::cast);
  }

  /**
   * Find all sink handles.
   *
   * @param clazz handle class
   * @param <D> device class type
   * @return stream of handles
   */
  public <D extends Device> Stream<Handle<D>> findAllSinkHandles(
      Class<? extends Handle<D>> clazz) {
    Optional<HandleProvider> provider = findOneProvider(clazz);
    if (provider.isPresent()) {
      return provider.get().findAllSinks().map(clazz::cast);
    } else {
      return Stream.empty();
    }
  }

  /**
   * Find one sink.
   *
   * @param clazz handle class
   * @param <D> device class type
   * @return sink
   */
  public <D extends Device> Optional<D> findOneSink(Class<? extends Handle<D>> clazz) {
    return findAllSinkHandles(clazz)
        .map(handle -> {
          try {
            return handle.open();
          } catch (DeviceUnavailableException e) {
            LOG.warn(e.getMessage());
            return null;
          }
        })
        .filter(Objects::nonNull).findFirst();
  }

  public <D extends Device> D findOneSinkOrThrow(Class<? extends Handle<D>> clazz)
      throws DeviceUnavailableException {
    return findOneSink(clazz).orElseThrow(DeviceUnavailableException::new);
  }


  /**
   * Find all source handles.
   *
   * @param clazz handle class
   * @param <D> device class type
   * @return stream of handles
   */
  public <D extends Device> Stream<Handle<D>> findAllSourceHandles(
      Class<? extends Handle<D>> clazz) {
    Optional<HandleProvider> provider = findOneProvider(clazz);
    if (provider.isPresent()) {
      return provider.get().findAllSources().map(clazz::cast);
    } else {
      return Stream.empty();
    }
  }

  /**
   * Find one source of a given handle class.
   *
   * @param clazz device class
   * @param <D> device class type
   * @return handle of source
   */
  public <D extends Device> Optional<Handle<D>> findOneSourceHandle(
      Class<? extends Handle<D>> clazz) {
    return findOneProvider(clazz)
        .flatMap(HandleProvider::findOneSource)
        .map(clazz::cast);
  }

  /**
   * Find one source.
   *
   * @param clazz handle class
   * @param <D> device class type
   * @return sink
   */
  public <D extends Device> Optional<D> findOneSource(Class<? extends Handle<D>> clazz) {
    return findAllSourceHandles(clazz)
        .map(handle -> {
          try {
            return handle.open();
          } catch (DeviceUnavailableException e) {
            LOG.warn(e.getMessage());
            return null;
          }
        })
        .filter(Objects::nonNull).findFirst();
  }

  public <D extends Device> D findOneSourceOrThrow(Class<? extends Handle<D>> clazz)
      throws DeviceUnavailableException {
    return findOneSink(clazz).orElseThrow(DeviceUnavailableException::new);
  }

  private Optional<HandleProvider> findOneProvider(Class<? extends Handle> clazz) {
    return providers.stream()
        .filter(provider ->
            provider.getHandleClasses().stream().anyMatch(clazz::isAssignableFrom))
        .findFirst();
  }

  public boolean noneMatch(Class<? extends Handle> clazz) {
    return handles.stream().noneMatch(clazz::isInstance);
  }


  public void dump() {
    dump("ENVIRONMENT");
  }

  /**
   * Dump environment information.
   */
  public void dump(String title) {
    LOG.info("\n"
        + title + "\n"
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
      AtomicInteger i = new AtomicInteger(0);
      for (Class<? extends Handle> clazz : clazzes) {
        appendInfo(clazz, i, sb, withConnections);
      }
    }
    return sb.toString();
  }

  private void appendInfo(Class<? extends Handle> clazz, AtomicInteger i, StringBuilder sb,
                          boolean withConnections) {
    sb.append("\n ** ").append(clazz.getSimpleName()).append(" **\n");
    for (Handle handle : getHandles(clazz)) {
      appendInfo(handle, i.getAndIncrement(), sb,  withConnections);
    }
  }

  private void appendInfo(Handle handle, int i, StringBuilder sb, boolean withConnections) {
    sb.append('\n').append(i).append(") ");
    sb.append(handle.isSink() ? " <" : "  ");
    sb.append(handle.isEnabled() ? "+" : "-");
    sb.append(handle.isSource() ? "> " : "  ");
    handle.appendTo(sb);
    if (withConnections) {
      try {
        Device device = handle.open();
        sb.append("\n          ").append(device.getSummary());
        device.getProperties().forEach((key, value) ->
            sb.append(String.format("\n%50s = %-40s", key, value))
        );
      } catch (DeviceUnavailableException e) {
        LOG.error("Cannot get device " + handle, e);
      }
    }
  }

  @Override
  public String toString() {
    return asString(false);
  }
}
