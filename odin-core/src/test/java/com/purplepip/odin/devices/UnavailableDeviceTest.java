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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.ToString;
import org.junit.jupiter.api.Test;

class UnavailableDeviceTest {
  @Test
  void testUnavailableDevice() {
    Handle handle = mock(Handle.class);
    when(handle.getName()).thenReturn("mock-handle");
    UnavailableDevice device = new UnavailableDevice(handle);
    assertEquals("Device for mock-handle is not available", device.getSummary());
  }

  @Test
  void testToString() {
    UnavailableDevice device = new UnavailableDevice(new TestHandle());
    assertEquals("UnavailableDevice(handle=UnavailableDeviceTest.TestHandle())",
        device.toString());
  }

  @ToString
  private static class TestHandle extends AbstractHandle {

    @Override
    public String getName() {
      return "name";
    }

    @Override
    public String getVendor() {
      return "vendor";
    }

    @Override
    public String getDescription() {
      return "description";
    }

    @Override
    public String getType() {
      return "type";
    }

    @Override
    public Device open() throws DeviceUnavailableException {
      throw new DeviceUnavailableException("Cannot open test handle");
    }

    @Override
    public boolean isSource() {
      return false;
    }

    @Override
    public boolean isSink() {
      return false;
    }
  }
}