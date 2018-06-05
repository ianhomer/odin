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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AbstractHandleProviderTest {
  @Test
  public void shouldFindOne() {
    HandleProvider handleProvider = new MockHandleProvider(
        true, true,
        asHandleList("TTTAAA", "TFTCCC"),
        asHandleList("FTTBBB", "TTTAAA")
    );
    Optional<Handle> sink = handleProvider.findOneSink();
    assertTrue(sink.isPresent());
    assertEquals("Sink not correct", "TTTAAA", sink.get().getName());

    Optional<Handle> source = handleProvider.findOneSource();
    assertTrue(source.isPresent());
    assertEquals("Source not correct", "FTTBBB", source.get().getName());
  }

  @Test
  public void shouldFindOneDifferentOrder() {
    HandleProvider handleProvider = new MockHandleProvider(
        true, true,
        asHandleList("TFTCCC", "TTTAAA"),
        asHandleList("TTTAAA", "FTTBBB")
    );
    Stream<Handle> handles = handleProvider.findAllSinks();
    assertEquals(5, handles.count());
    Optional<Handle> sink = handleProvider.findOneSink();
    assertTrue(sink.isPresent());
    assertEquals("Sink not correct", "TFTCCC", sink.get().getName());

    Optional<Handle> source = handleProvider.findOneSource();
    assertTrue(source.isPresent());
    assertEquals("Source not correct", "TTTAAA", source.get().getName());
  }

  @Test
  public void shouldFindOneNotInPriorityList() {
    HandleProvider handleProvider = new MockHandleProvider(
        true, true,
        asHandleList("XXX", "YYY"),
        asHandleList("XXX", "YYY")
    );

    Stream<Handle> handles = handleProvider.findAllSinks();
    assertEquals(5, handles.count());
    LOG.info("All sinks {}", handleProvider.findAllSinks().collect(Collectors.toList()));
    Optional<Handle> sink = handleProvider.findOneSink();
    assertTrue(sink.isPresent());
    assertEquals("Sink not correct", "TFTCCC", sink.get().getName());

    Optional<Handle> source = handleProvider.findOneSource();
    assertTrue(source.isPresent());
    assertEquals("Source not correct", "FTTBBB", source.get().getName());
  }

  @Test
  public void shouldNotFindOne() {
    HandleProvider handleProvider = new MockHandleProvider(
        false, false,
        asHandleList(),
        asHandleList()
    );
    Optional<Handle> sink = handleProvider.findOneSink();
    assertFalse("Should not find sink, but found " + sink, sink.isPresent());

    Optional<Handle> source = handleProvider.findOneSource();
    assertFalse("Should not find source, but found " + source, source.isPresent());
  }
}