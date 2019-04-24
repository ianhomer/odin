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

package com.purplepip.odin.boot;

import static org.junit.Assert.assertFalse;

import com.purplepip.odin.devices.Environment;
import org.junit.Test;

public class OptionalMidiApplicationTest {
  @Test
  public void testNoSinkNoSource() {
    var application = new OptionalMidiApplication(new Environment());
    assertFalse(application.getSink().isPresent());
    assertFalse(application.getSource().isPresent());
    assertFalse(application.getSynthesizer().isPresent());
  }
}