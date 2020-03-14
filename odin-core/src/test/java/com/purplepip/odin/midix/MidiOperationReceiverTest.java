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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.purplepip.odin.music.operations.NoteOnOperation;
import javax.sound.midi.MidiMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class MidiOperationReceiverTest {
  @Test
  void send() throws Exception {
    MidiMessageReceiver deviceReceiver = spy(MidiMessageReceiver.class);
    MidiOperationReceiver operationReceiver = new MidiOperationReceiver(deviceReceiver);
    operationReceiver.onPerformanceStart();
    verify(deviceReceiver).onPerformanceStart();
    operationReceiver.handle(new NoteOnOperation(1,2,3), 10);
    ArgumentCaptor<MidiMessage> message = ArgumentCaptor.forClass(MidiMessage.class);
    ArgumentCaptor<Long> time = ArgumentCaptor.forClass(Long.class);
    verify(deviceReceiver).send(message.capture(), time.capture());
    assertEquals(145, message.getValue().getStatus());
    assertEquals(10L, time.getValue().longValue());
    operationReceiver.onPerformanceStop();
    verify(deviceReceiver).onPerformanceStop();
  }
}
