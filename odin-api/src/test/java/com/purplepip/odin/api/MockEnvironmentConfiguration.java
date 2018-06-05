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

package com.purplepip.odin.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.purplepip.odin.devices.AbstractDevice;
import com.purplepip.odin.devices.AbstractHandle;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.devices.Handle;
import com.purplepip.odin.devices.HandleProvider;
import java.util.Set;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Order(2)
public class MockEnvironmentConfiguration {
  /**
   * Create mock odin environment.
   *
   * @return mock odin environment
   */
  @Bean
  public Environment odinEnvironment() {
    HandleProvider provider = mock(HandleProvider.class);
    Set<Handle> handles = Sets.newLinkedHashSet(Sets.newHashSet(
        mockHandle(1),
        mockHandle(2),
        mockHandle(3),
        mockHandle(4)
    ));
    when(provider.getSinkHandles()).thenReturn(handles);
    return new Environment(provider);
  }

  private Handle mockHandle(int index) {
    return mockHandle("name" + index, "vendor" + index, "description" + index);
  }

  private Handle mockHandle(String name, String vendor, String description) {
    MockHandle handle = new MockHandle();
    handle.setDescription(description);
    handle.setName(name);
    handle.setVendor(vendor);
    handle.setType("mock");
    return handle;
  }

  @Data
  @ToString(callSuper = true)
  private final class MockHandle extends AbstractHandle<MockDevice> {
    private String description;
    private String name;
    private String vendor;
    private String type;

    @Override
    public MockDevice open() {
      return new MockDevice(this);
    }
  }

  @ToString
  private final class MockDevice extends AbstractDevice {
    private MockHandle handle;

    public MockDevice(MockHandle handle) {
      initialise();
      this.handle = handle;
    }

    @Override
    public String getName() {
      return "MockDevice";
    }

    @Override
    public String getSummary() {
      return getName();
    }

    @Override
    public Handle getHandle() {
      return handle;
    }

    @Override
    public void close() {
      // No Operation
    }

    @Override
    public boolean isOpen() {
      return true;
    }

    @Override
    public boolean isSource() {
      return false;
    }

    @Override
    public boolean isSink() {
      return false;
    }

    @Override
    public void initialise() {
      setProperty("property", "value");
    }

  }
}
