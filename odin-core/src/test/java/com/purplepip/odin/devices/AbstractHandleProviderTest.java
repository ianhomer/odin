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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Test;

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

  private static class MockHandleProvider extends AbstractHandleProvider {
    private final boolean hasSinks;
    private final boolean hasSources;

    public MockHandleProvider(boolean hasSinks, boolean hasSources,
        List<Handle> prioritisedSinks, List<Handle> prioritisedSources) {
      super(prioritisedSinks, prioritisedSources);
      this.hasSinks = hasSinks;
      this.hasSources = hasSources;
    }

    @Override
    protected Stream<Handle> getHandleStream() {
      Stream.Builder<Handle> builder = Stream.builder();
      if (hasSinks) {
        builder.accept(createHandle("TFTCCC", true, false, true));
      }
      if (hasSources) {
        builder.accept(createHandle("FTTBBB", false, true, true));
      }
      if (hasSinks && hasSources) {
        builder
            .add(createHandle("TTTAAA", true, true, true))
            .add(createHandle("TTFDDD", true, true, false))
            .add(createHandle("TTTEEE", true, true, true))
            .add(createHandle("TTTFFF", true, true, true));
      }
      return builder.build();
    }

    @Override
    public Set<Class<? extends Handle>> getHandleClasses() {
      return null;
    }

    private static Handle createHandle(String name, boolean sink, boolean source, boolean enabled) {
      Handle handle = mock(Handle.class);
      when(handle.getName()).thenReturn(name);
      when(handle.isSink()).thenReturn(sink);
      when(handle.isSource()).thenReturn(source);
      when(handle.isEnabled()).thenReturn(enabled);
      when(handle.toString()).thenReturn(name);
      return handle;
    }
  }
}