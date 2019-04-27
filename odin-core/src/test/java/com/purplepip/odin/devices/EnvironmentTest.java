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

import static com.purplepip.odin.devices.NamedHandle.asHandleList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.system.Environments;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class EnvironmentTest {
  @Test
  void testDump() {
    try (LogCaptor captor = new LogCapture().info().from(Environment.class).start()) {
      Environments.newEnvironment().dump();
      assertEquals("Environment log messages not as expected " + captor, 1, captor.size());
    }
  }

  @Test
  void shouldFindOne() {
    Environment environment = new Environment(new MockHandleProvider(
        true, true,
        asHandleList("TTTAAA", "TFTCCC"),
        asHandleList("FTTBBB", "TTTAAA")
    ));
    Optional<Handle<MockDevice>> sink = environment.findOneSinkHandle(MockHandle.class);
    assertTrue(sink.isPresent());
    assertEquals("Sink not correct", "TTTAAA", sink.get().getName());

    Optional<Handle<MockDevice>> source = environment.findOneSourceHandle(MockHandle.class);
    assertTrue(source.isPresent());
    assertEquals("Source not correct", "FTTBBB", source.get().getName());
  }

  @Test
  void shouldNotFindOneSink() {
    Environment environment = new Environment(new MockHandleProvider(
        true, true,
        asHandleList(), asHandleList(), (handle) -> "TTTXXX".equals(handle.getName())
    ));
    assertThrows(DeviceUnavailableException.class, () ->
        environment.findOneSinkOrThrow(MockHandle.class)
    );
  }

  @Test
  void shouldNotFindOneSource() {
    Environment environment = new Environment(new MockHandleProvider(
        true, true,
        asHandleList(), asHandleList(), (handle) -> "TTTXXX".equals(handle.getName())
    ));
    assertThrows(DeviceUnavailableException.class, () ->
        environment.findOneSourceOrThrow(MockHandle.class)
    );
  }

  @Test
  void shouldOpenSink() {
    Environment environment = new Environment(
        new MockHandleProvider(true, false,
            asHandleList("TTTXXX", "TFTCCC"),
            asHandleList()
            ));
    Stream<Handle<MockDevice>> handles = environment.findAllSinkHandles(MockHandle.class);
    Optional<Handle<MockDevice>> handle = handles.findFirst();
    assertTrue(handle.isPresent());
    assertEquals("TTTXXX", handle.get().getName());

    Optional<MockDevice> device;
    try (LogCaptor captor = new LogCapture().warn().from(Environment.class).start()) {
      device = environment.findOneSink(MockHandle.class);
      assertEquals("Device should fail to open with warning " + captor, 1, captor.size());
    }
    assertTrue(device.isPresent());
    assertEquals("TFTCCC", device.get().getName());
  }

  @Test
  void shouldOpenSource() {
    Environment environment = new Environment(
        new MockHandleProvider(true, true,
            asHandleList(),
            asHandleList("TTTXXX", "FTTBBB")
        ));
    Stream<Handle<MockDevice>> handles = environment.findAllSourceHandles(MockHandle.class);
    Optional<Handle<MockDevice>> handle = handles.findFirst();
    assertTrue(handle.isPresent());
    assertEquals("TTTXXX", handle.get().getName());

    Optional<MockDevice> device;
    try (LogCaptor captor = new LogCapture().warn().from(Environment.class).start()) {
      device = environment.findOneSource(MockHandle.class);
      assertEquals("Device should fail to open with warning " + captor, 1, captor.size());
    }
    assertTrue(device.isPresent());
    assertEquals("FTTBBB", device.get().getName());
  }
}