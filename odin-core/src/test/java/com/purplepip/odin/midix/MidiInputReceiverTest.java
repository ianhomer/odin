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

package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.DefaultMicrosecondPositionProvider;
import com.purplepip.odin.midi.Status;
import com.purplepip.odin.operation.OperationHandler;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.ShortMessage;
import org.junit.jupiter.api.Test;

class MidiInputReceiverTest {
  @Test
  void testSend() throws Exception {
    final AtomicInteger count = new AtomicInteger();
    OperationHandler operationReceiver = ((operation, time) -> count.incrementAndGet());
    MidiInputReceiver midiInputReceiver = new MidiInputReceiver(
        operationReceiver, new DefaultMicrosecondPositionProvider());
    midiInputReceiver.send(new ShortMessage(Status.NOTE_ON.getValue(), 60, 50), -1);
    assertEquals(1, count.get());
  }
}